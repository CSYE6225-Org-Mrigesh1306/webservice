# Assignment-6: IAM Users, Roles & Policies(1)

## Load Balancer Security Group :
- Create a security group for the load balancer to access the web application.
- Add ingress rule to allow TCP traffic on ports 80, and 443 from anywhere in the world.
- This security group will be referred to as the load balancer security group.

## AutoScaling Application Stack

| Key                      |   Value                                |
|--------------------------|----------------------------------------|
| ImageId                  | Your custom AMI                        |
| Instance Type            | t2.micro                               |
| KeyName                  | YOUR_AWS_KEYNAME                       |
| AssociatePublicIpAddress | True                                   |
| UserData                 | SAME_USER_DATA_AS_CURRENT_EC2_INSTANCE |
| IAM Role                 | SAME_AS_CURRENT_EC2_INSTANCE           |
| Resource Name            | asg_launch_config                    |
| Security Group           | WebAppSecurityGroup                    |



## AutoScaling Group

| Paramter                 |   Value           |
|--------------------------|-------------------|
| Cooldown                 | 60                |
| LaunchConfigurationName  | asg_launch_config |
| MinSize                  | 1                 |
| MaxSize                  | 5                 |
| DesiredCapacity          | 1                 |


## AutoScaling Policies
- Scale up policy when average CPU usage is above 5%. Increment by 1.
- Scale down policy when average CPU usage is below 3%. Decrement by 1.

## Setup Application Load Balancer For Your Web Application
- EC2 instances launched in the auto-scaling group should now be load balanced.
- Add a balancer resource to your CloudFormation template.
- Setup Application load balancer to accept HTTP traffic on port 80 and forward it to your application instances on whatever port it listens on.
- Attach the load balancer security group to the load balancer.


## DNS Updates
- Route53 should be updated from the CloudFormation template.
- Route53 resource record for your domain name should now be an alias for your load balancer application.
- The CloudFormation template should configure Route53 so that your domain points to your load balancer and your web application is accessible thru http://your-domain-name.tld/
- Your application must be accessible using root context i.e. http://your-domain-name.tld/ (Links to an external site.) and not http://your-domain-name.tld/app-0.1/



