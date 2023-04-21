#!/bin/bash

for i in {25506..25515} ; do lsof -i :"$i" ; done