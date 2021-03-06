#!/usr/bin/env bash

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e -o pipefail

BENCHMARK_BRANCH='develop'
BRANCH='develop'

TEMP=`getopt t:b:v:e:B:V:m:o:h "$@"`
eval set -- "$TEMP"

while true ; do
    case "$1" in
        -t)
            TAG=$2 ; shift 2 ;;
        -e)
            BENCHMARK_BRANCH=$2 ; shift 2 ;;
        -m)
            METADATA=$2 ; shift 2 ;;
        -o)
            OUTPUT=$2 ; shift 2 ;;
        -b)
            BRANCH=$2 ; shift 2 ;;
        -v)
            VERSION=$2 ; shift 2 ;;
        -B)
            BASELINE_BRANCH=$2 ; shift 2 ;;
        -V)
            BASELINE_VERSION=$2 ; shift 2 ;;
        -h)
            echo "Usage: run_test.sh -t [tag] [-v [version] | -b [branch]] [-V [baseline version] | -B [baseline branch]] <options...>"
            echo "Options:"
            echo "-e : Benchmark branch (optional - defaults to develop)"
            echo "-o : Output directory (optional - defaults to ./output-<date>-<tag>)"
            echo "-v : Geode Version"
            echo "-b : Geode Branch"
            echo "-V : Geode Baseline Version"
            echo "-B : Geode Baseline Branch"
            echo "-t : Cluster tag"
            echo "-m : Test metadata to output to file, comma-delimited (optional)"
            echo "-h : This help message"
            shift 2
            exit 1 ;;
        --) shift ; break ;;
        *) echo "Internal error!" ; exit 1 ;;
    esac
done



DATE=$(date '+%m-%d-%Y-%H-%M-%S')

if [ -z "${TAG}" ]; then
  echo "--tag argument is required."
  exit 1
fi

if [ -z "${METADATA}" ]; then
  METADATA="'geode branch':'${BRANCH}','geode version':'${VERSION}','baseline branch':'${BASELINE_BRANCH}','baseline version':'${BASELINE_VERSION}','benchmark branch':'${BENCHMARK_BRANCH}'"
fi

OUTPUT=${OUTPUT:-output-${DATE}-${TAG}}

set -x
if ! [[ "$OUTPUT" = /* ]]; then
  OUTPUT="$(pwd)/${OUTPUT}"
fi

if [ -z "${VERSION}" ]; then
  ./run_tests.sh -t ${TAG} -b ${BRANCH} -e ${BENCHMARK_BRANCH} -o ${OUTPUT}/branch -m ${METADATA}
else
  ./run_tests.sh -t ${TAG} -v ${VERSION} -e ${BENCHMARK_BRANCH} -o ${OUTPUT}/branch -m ${METADATA}
fi

if [ -z "${BASELINE_VERSION}" ]; then
./run_tests.sh -t ${TAG} -b ${BASELINE_BRANCH} -e ${BENCHMARK_BRANCH} -o ${OUTPUT}/baseline -m ${METADATA}
else
./run_tests.sh -t ${TAG} -v ${BASELINE_VERSION} -e ${BENCHMARK_BRANCH} -o ${OUTPUT}/baseline -m ${METADATA}
fi
set +x

./analyze_tests.sh ${OUTPUT}
