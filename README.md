# Assignment-10: Secure Application Endpoints

* Secure your web application endpoints with valid SSL certificates
    * For dev environment, you may use the AWS Certificate Manager (Links to an external site.) service to get SSL certificates.
    * For prod environment, you must request an SSL certificate from Namecheap (Links to an external site.) or any other SSL vendor except for AWS Certificate Manager, import it into AWS Certificate Manager (Links to an external site.) from your CLI, and then configure your load balancer to use the imported certificate.
* Plain text requests sent to HTTP do not have to be supported
    * HTTP to HTTPS redirection is not required.
* Traffic from the load balancer to the EC2 instance can use plain text protocol such as HTTP
* Users should not be able to connect to the EC2 instance directly.

## Secure Database Connections
* Use Secure Socket Layer (SSL) or Transport Layer Security (TLS) for encrypted connections from your application to the RDS instance.

## Command to import Certificate
* aws acm import-certificate --certificate fileb://demo_mrigeshdasgupta_me.crt --certificate-chain fileb://demo_mrigeshdasgupta_me.ca-bundle --private-key fileb://demo.pem --profile=demo

