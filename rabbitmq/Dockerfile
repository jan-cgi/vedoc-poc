FROM rabbitmq:4.3.0-management-alpine

# EXTERNAL maps the mTLS peer certificate to a RabbitMQ user.
RUN rabbitmq-plugins enable --offline rabbitmq_auth_mechanism_ssl
