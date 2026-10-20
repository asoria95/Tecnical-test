variable "aws_region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "EC2 instance type. t3.small (2GB) is the minimum recommended: Gradle multi-stage builds + 2x Postgres + 2x Spring Boot need ~1.5GB."
  type        = string
  default     = "t3.small"
}

variable "ssh_public_key_path" {
  description = "Local path to the SSH public key file to install on the EC2 instance (e.g. ~/.ssh/id_rsa.pub)"
  type        = string
}

variable "allowed_ssh_cidr" {
  description = "CIDR block allowed to SSH into the instance. Restrict to your IP in production (e.g. '1.2.3.4/32')."
  type        = string
  default     = "0.0.0.0/0"
}

variable "project_name" {
  description = "Short name used for resource naming and tagging"
  type        = string
  default     = "banking-challenge"
}
