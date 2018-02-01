#!/bin/bash -e

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
else
	echo "Started with deletion of resources using cloud formation"
fi

RC=$(aws cloudformation describe-stacks --stack-name $1 --query Stacks[0].StackId --output text) || echo "Stack $1 doesn't exist"; exit 0

echo "Deleting stack: $RC"

aws cloudformation delete-stack --stack-name $1

echo "Stack deletion in progress. Please wait"
RC=$(aws cloudformation wait stack-delete-complete --stack-name $1)

if [ $? -eq 0 ]
then
  echo "Stack deletion complete"
  exit 0
else
 	echo "Fail Stack deleted"
 	exit 1
fi
