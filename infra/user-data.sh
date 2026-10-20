#!/bin/bash
# Cloud-init bootstrap script — runs once on first boot.
# Installs Docker Engine and the Docker Compose plugin on Ubuntu 22.04 LTS.

set -euo pipefail

apt-get update -y
apt-get install -y \
  ca-certificates \
  curl \
  gnupg \
  lsb-release \
  git \
  rsync

# Add Docker's official GPG key and repository
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
  > /etc/apt/sources.list.d/docker.list

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Enable and start Docker
systemctl enable --now docker

# Allow the default 'ubuntu' user to run Docker without sudo
usermod -aG docker ubuntu

# Ensure /app directory exists for project deployment
mkdir -p /app
chown ubuntu:ubuntu /app
