# Demo-Assignment-9: AWS Simple Notification Service (SNS) & AWS Lambda Function

## AMI Updates :
* Update your packer template to install. the Unified CloudWatch Agent in your AMIs.
* Your CloudWatch agent must be set up to start automatically when an EC2 instance is launched using your AMI.
* Note that until CodeDeploy deploys the application on the EC2 instance, the CloudWatch agent may not work 
    * You should restart the CloudWatch agent in your after install CodeDeploy lifecycle hook.

## WebApp User Stories
* As a user, I want all application log data to be available in CloudWatch.
* As a user, I want metrics on API usage available in CloudWatch.
* Create the following custom metrics for every API we have implemented in the web application. The metrics data should be collected in CloudWatch.
    * Count the number of times each API is called.
* You can retrieve custom metrics using either StatsD
* CloudWatch agent configuration file must be copied over to the EC2 server when the application is being deployed by CodeDeploy.
* RE) Configure the CloudWatch agent before starting your service in the afterInstall hook of the CodeDeploy lifecycle.


## CodeDeploy Application
- Application Name - **csye6225-webapp**
- Compute Platform - **EC2/On-premises**

## CodeDeploy Deployment Group
- Deployment group name - **csye6225-webapp-deployment**
- Service role - **CodeDeployServiceRole**
- Deployment type - **In-place**
- Environment Configuration - **Amazon EC2 Instances**
- Deployment settings - **CodeDeployDefault.AllAtOnce**
- Load Balancer - **disabled**
- Rollback - **Rollback when a deployment fails**
- Everything else can be left to default values.
