output "instance_public_ip" {
  description = "Elastic IP address of the EC2 instance"
  value       = aws_eip.app.public_ip
}

output "ssh_command" {
  description = "Ready-to-use SSH command to connect to the instance"
  value       = "ssh -i ~/.ssh/id_rsa ubuntu@${aws_eip.app.public_ip}"
}

output "customer_service_url" {
  description = "Base URL for customer-service"
  value       = "http://${aws_eip.app.public_ip}:8081"
}

output "account_service_url" {
  description = "Base URL for account-service"
  value       = "http://${aws_eip.app.public_ip}:8080"
}
