#!/bin/bash

for i in {25506..25515} ; do fuser --kill "$i"/tcp ; done