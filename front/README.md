# Yoga

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.16.

## Start the project

Git clone:

> git clone https://github.com/OpenClassrooms-Student-Center/P5-Full-Stack-testing

Go inside folder:

> cd yoga

Install dependencies:

> npm install

Launch Front-end:

> npm run start;

### Test

#### E2E

- Launch back with e2e db (db will reset seed data everytime it starts):
  `mvn spring-boot:run "-Dspring-boot.run.profiles=e2e"`
- Launch all e2e tests:
  `npm run e2e:ci`
- Generate coverage report (you should launch e2e test before):
  `npm run e2e:coverage`
- Stop the back. If you don't stop the back before launching tests again, tests will fail because db has not been reset.

Coverage report is available here:
[front/coverage/lcov-report/index.html](coverage/lcov-report/index.html)

#### Unitary test

- Launch tests:
  `npm run test`
- for following change:
  `npm run test:watch`

Coverage report is available here:
[front/coverage/jest/lcov-report/index.html](coverage/jest/lcov-report/index.html)
