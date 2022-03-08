packer {
  required_plugins {
    amazon = {
      version = ">= 0.0.1"
      source  = "github.com/hashicorp/amazon"
    }
  }
}
variable "github_repo" {
  default = env("GITHUB_REPO_PATH")
}
variable "aws_access_key"{
  type= string
  default =env("AWS_ACCESS_KEY_ID")
}

variable "aws_secret_key"{
  type = string
  default =env("AWS_SECRET_ACCESS_KEY")
}

variable "source_ami" {
  type    = string
  default = "ami-033b95fb8079dc481"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}
variable "aws_region" {
  type    = string
  default = "us-east-1"
}

locals { timestamp = regex_replace(timestamp(), "[- TZ:]", "") }

source "amazon-ebs" "ami-image" {
  access_key      = "${var.aws_access_key}"
  ami_description = "Amazon Linux 2 AMI for CSYE 6225"
  ami_name        = "AMI-Csye6225-${local.timestamp}"
  ami_users = ["170773480295"]
  instance_type   = "t2.micro"
  source_ami_filter {
    filters = {
      virtualization-type = "hvm"
      name                = "amzn2-ami-kernel-5.10-hvm*"
      root-device-type    = "ebs"
    }
    owners      = ["amazon"]
    most_recent = true
  }
  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 8
    volume_type           = "gp2"
  }
  region       = "${var.aws_region}"
  secret_key   = "${var.aws_secret_key}"
  source_ami   = "${var.source_ami}"
  ssh_username = "${var.ssh_username}"
  
}
build {
  sources = [
    "source.amazon-ebs.ami-image"
  ]
  provisioner "file" {
    destination = "/tmp/webapp.service"
    source      = "${var.github_repo}/packer/webapp.service"
  }
  provisioner "file" {
      destination = "/tmp/WebApp-0.0.1-SNAPSHOT.jar"
      source      = "${var.github_repo}/target/WebApp-0.0.1-SNAPSHOT.jar"
  }
  provisioner "shell" {
    inline = [
      "sleep 30",
      "sudo yum update -y"
      "echo Java Installation"
      "sudo amazon-linux-extras install java-openjdk11",
      "sudo yum install maven -y",
      "java -version",
      "sudo yum -y install https://dev.mysql.com/get/mysql80-community-release-el7-5.noarch.rpm",
      "sudo amazon-linux-extras install epel",
      "sudo yum -y install mysql-community-server",
      "sudo systemctl enable --now mysqld",
      "systemctl status mysqld",
      "pass=$(sudo grep 'temporary password' /var/log/mysqld.log | awk {'print $13'})",
      "mysql --connect-expired-password -u root -p$pass -e \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'Mrig@1306';\"",
      "mysql -u root -pMrig@1306 -e \"create database book_mgnt;\"",
      "pwd",
      "mkdir webservice-target",
      "cd webservice-target",
      "sudo cp /tmp/WebApp-0.0.1-SNAPSHOT.jar WebApp-0.0.1-SNAPSHOT.jar",
      "sudo cp /tmp/webapp.service /etc/systemd/system/",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable webapp.service",
      "sudo systemctl start webapp.service",
      "sudo systemctl status webapp.service"
    ]
  }
}