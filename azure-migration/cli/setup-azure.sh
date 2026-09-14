#!/usr/bin/env bash
set -euo pipefail

source "$(dirname "$0")/azure.env"

echo "Azure location:       $AZ_LOCATION"
echo "Resource group:       $AZ_RESOURCE_GROUP"
echo "ACR:                  $AZ_ACR_NAME"
echo "AKS:                  $AZ_AKS_NAME"

echo
echo "Subscription:"
az account show --query "{name:name,id:id,state:state}" -o table

echo
echo "Azure CLI configuration ready."
