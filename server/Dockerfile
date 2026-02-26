#
# Copyright 2025 Hochschule Luzern Informatik.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM amazoncorretto:21-alpine
LABEL maintainer="Joel Muller"
LABEL version="1.0.0"
LABEL description="Server side for the World Quiz Application"
RUN adduser -D worker
USER worker
WORKDIR /opt/world-quiz
CMD ["java","-jar","world-quiz.jar"]
COPY target/world-quiz.jar /opt/world-quiz/world-quiz.jar
COPY data data
