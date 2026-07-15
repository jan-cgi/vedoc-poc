#!/usr/bin/env bash

# Creates an intentionally local, self-signed PKI for the vedoc POC.
# Do not use these credentials outside development.

set -euo pipefail

readonly DEFAULT_OUTPUT_DIR="certs"
readonly CA_VALIDITY_DAYS=3650
readonly LEAF_VALIDITY_DAYS=825
readonly PKCS12_PASSWORD="changeit"

output_dir="$DEFAULT_OUTPUT_DIR"
force=false

usage() {
  cat <<'EOF'
Usage: ./scripts/generate-dev-certs.sh [options]

Options:
  --output <directory>  Write certificates to this directory (default: certs)
  --force               Replace an existing output directory
  -h, --help            Show this help

The script creates a private POC CA, server certificates for RabbitMQ and
IBM MQ, and client certificates for vedoc, xml-json-adapter, and one extra
client per broker. Java-ready PKCS#12 key- and truststores are included.
Their password is the fixed POC value: changeit.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --output)
      [[ $# -ge 2 ]] || { echo "--output requires a directory" >&2; exit 1; }
      output_dir="$2"
      shift 2
      ;;
    --force)
      force=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

command -v openssl >/dev/null || { echo "openssl is required" >&2; exit 1; }
command -v keytool >/dev/null || { echo "keytool is required" >&2; exit 1; }

if [[ -e "$output_dir" ]]; then
  if [[ "$force" != true ]]; then
    echo "Output directory '$output_dir' already exists. Use --force to replace it." >&2
    exit 1
  fi
  rm -rf "$output_dir"
fi

umask 077
mkdir -p "$output_dir"/{ca,rabbitmq,ibmmq,clients}

ca_key="$output_dir/ca/poc-ca.key"
ca_cert="$output_dir/ca/poc-ca.crt"

openssl genrsa -out "$ca_key" 4096
openssl req -x509 -new -sha256 -days "$CA_VALIDITY_DAYS" \
  -key "$ca_key" \
  -out "$ca_cert" \
  -subj "/C=DE/O=vedoc POC/CN=vedoc-poc-root-ca" \
  -addext "basicConstraints=critical,CA:true" \
  -addext "keyUsage=critical,keyCertSign,cRLSign" \
  -addext "subjectKeyIdentifier=hash"

create_server_certificate() {
  local name="$1"
  local san="$2"
  local destination="$output_dir/$name"

  openssl genrsa -out "$destination/server.key" 2048
  openssl req -new -sha256 \
    -key "$destination/server.key" \
    -out "$destination/server.csr" \
    -subj "/C=DE/O=vedoc POC/CN=$name" \
    -addext "subjectAltName=$san" \
    -addext "basicConstraints=critical,CA:false" \
    -addext "keyUsage=critical,digitalSignature,keyEncipherment" \
    -addext "extendedKeyUsage=serverAuth"
  openssl x509 -req -sha256 -days "$LEAF_VALIDITY_DAYS" \
    -in "$destination/server.csr" \
    -CA "$ca_cert" \
    -CAkey "$ca_key" \
    -CAcreateserial \
    -out "$destination/server.crt" \
    -copy_extensions copy
  rm "$destination/server.csr"

  # Useful for Java tooling and for importing into the IBM MQ key repository.
  openssl pkcs12 -export \
    -inkey "$destination/server.key" \
    -in "$destination/server.crt" \
    -certfile "$ca_cert" \
    -out "$destination/server.p12" \
    -name "$name" \
    -passout "pass:$PKCS12_PASSWORD"
}

create_client_certificate() {
  local name="$1"
  local destination="$output_dir/clients/$name"

  mkdir -p "$destination"
  openssl genrsa -out "$destination/client.key" 2048
  openssl req -new -sha256 \
    -key "$destination/client.key" \
    -out "$destination/client.csr" \
    -subj "/C=DE/O=vedoc POC/CN=$name" \
    -addext "basicConstraints=critical,CA:false" \
    -addext "keyUsage=critical,digitalSignature,keyEncipherment" \
    -addext "extendedKeyUsage=clientAuth"
  openssl x509 -req -sha256 -days "$LEAF_VALIDITY_DAYS" \
    -in "$destination/client.csr" \
    -CA "$ca_cert" \
    -CAkey "$ca_key" \
    -CAcreateserial \
    -out "$destination/client.crt" \
    -copy_extensions copy
  rm "$destination/client.csr"

  openssl pkcs12 -export \
    -inkey "$destination/client.key" \
    -in "$destination/client.crt" \
    -certfile "$ca_cert" \
    -out "$destination/client.p12" \
    -name "$name" \
    -passout "pass:$PKCS12_PASSWORD"
}

create_server_certificate "rabbitmq" "DNS:rabbitmq,DNS:localhost"
create_server_certificate "ibmmq" "DNS:ibmmq,DNS:localhost"

create_client_certificate "vedoc-rabbit"
create_client_certificate "xml-json-adapter-rabbit"
create_client_certificate "rabbit-test-client"
create_client_certificate "xml-json-adapter-ibmmq"
create_client_certificate "ibmmq-test-client"

# Java needs the CA as a trusted certificate entry. OpenSSL can produce a
# PKCS#12 container without private keys, but Java then sees no entries.
keytool -importcert -noprompt \
  -alias "vedoc-poc-root-ca" \
  -file "$ca_cert" \
  -keystore "$output_dir/truststore.p12" \
  -storetype PKCS12 \
  -storepass "$PKCS12_PASSWORD"

find "$output_dir" -type f \( -name '*.key' -o -name '*.p12' \) -exec chmod 600 {} +
find "$output_dir" -type f \( -name '*.crt' -o -name '*.srl' \) -exec chmod 644 {} +

cat <<EOF
Created development certificates in: $output_dir

RabbitMQ server: $output_dir/rabbitmq/server.{crt,key}
IBM MQ server:    $output_dir/ibmmq/server.{crt,key,p12}
CA certificate:   $ca_cert
Java truststore:  $output_dir/truststore.p12

Client certificates were created beneath: $output_dir/clients
EOF
