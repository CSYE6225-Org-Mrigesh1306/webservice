# Assignment-7: CI/CD for Web Application

## CI/CD WORKFLOW :
* Developer commits code changes to GitHub repository.
* GitHub Actions will trigger a new build on pull request merge.
* GitHub Actions will run the build steps from the GitHub Actions workflow. Build steps should do the following: 
    * Run the unit test.
    * Validate Packer Template
    * Build AMI (Note: New AMI image is not used in the CI/CD pipeline in this assignment)
          * Upgrade OS packages
          * Install dependencies (JAVA, MAVEN)
          * Install application dependencies
          * Copy application artifact from step 3.
    * Zip the artifacts and upload the zip archive to the CodeDeploy's S3 bucket.
    * Trigger a new CodeDeploy deployment with the latest revision of your artifact.

## IAM SETUP
* [CodeDeploy-EC2-S3]() policy allows EC2 instances to read data from S3 buckets. This policy is required for EC2 instances to download the latest
application revision.
* [GH-Code-Deploy]() policy allows GitHub Actions to call CodeDeploy APIs to initiate application deployment on EC2 instances.
* [GH-Upload-To-S3]() policy allows GitHub Actions to upload artifacts from the latest successful build to the dedicated S3 bucket used by CodeDeploy.



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
