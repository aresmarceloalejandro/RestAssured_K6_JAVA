import { check, sleep } from 'k6';
import { performRequest } from './CommonFuction.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const timeSleep = 1;

export function handleSummary(data) {
    return {
      "UserTest.html": htmlReport(data),
    };
  }

export const options = {
    scenarios: generateScenarios([
        'postUser',
        'createUserWithList',
        'loginUser',
        'logoutUser',
        'getUser',
        'updateUser',
        'deleteUser'
    ]),
};

function generateScenarios(functionNames) {
    let scenarios = {};
    functionNames.forEach((funcName) => {
        scenarios[`${funcName}Scenario`] = {
            executor: 'constant-vus',
            exec: funcName,
            vus: 1,
            duration: '1s',
        };
    });
    return scenarios;
}

export function postUser() {
    const jsonBody = JSON.stringify({
        "id": 11,
        "username": "theUser",
        "firstName": "John",
        "lastName": "James",
        "email": "john@email.com",
        "password": "12345",
        "phone": "12345",
        "userStatus": 1
      });

    const headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    };

    let res = performRequest('POST', '/user', jsonBody, headers, {}, 200);
    let emailValue = JSON.parse(res.body).email;
    check(res, {
        'email value is john@email.com in post User': (r) => emailValue === 'john@email.com',
    });

    // Negative Scenario
    performRequest('POST', '/user', 'null', headers, {}, 500);

    sleep(timeSleep);
}

export function createUserWithList() {
    const jsonBody = JSON.stringify([[{
        "id": 11,
        "username": "theUser",
        "firstName": "John",
        "lastName": "James",
        "email": "john@email.com",
        "password": "12345",
        "phone": "12345",
        "userStatus": 1
      }]]);

    const headers = {
        'accept': 'application/xml',
        'Content-Type': 'application/json'
    };

    let res = performRequest('POST', '/user/createWithList', jsonBody, headers, {}, 200);
    let usernameValue = res.body.match(/<username>([^<]+)<\/username>/)[1];
    check(res, {
        'username value is theUser in create User with List': (r) => usernameValue === 'theUser',
    });

    // Negative Scenario
    performRequest('POST', '/user/createWithList', 'null', headers, {}, 500);

    sleep(timeSleep);
}

export function loginUser() {
    const headers = {
        'accept': 'application/xml'
    };

    let res = performRequest('GET', '/user/login?username=string&password=string', null, headers, {}, 200);
    let loggedInUserSession = res.body.split('Logged in user session: ')[1]?.trim();
    
    check(res, {
        'loggedInUserSession is not null': (r) => loggedInUserSession !== undefined && loggedInUserSession !== null,
        'loggedInUserSession length is at least 17': (r) => loggedInUserSession && loggedInUserSession.length >= 17,
    });

    sleep(timeSleep);
}

export function logoutUser() {
    const headers = {
        'accept': '*/*'
    };

    let res = performRequest('GET', '/user/logout', null, headers, {}, 200);
    check(res, {
        'response contains User logged out': (r) => r.body.includes('User logged out'),
    });

    sleep(timeSleep);
}

export function getUser() {
    const username = 'theUser';
    const headers = {
        'accept': 'application/xml'
    };

    let res = performRequest('GET', `/user/${username}`, null, headers, {}, 200);
    let usernameValue = res.body.match(/<username>([^<]+)<\/username>/)[1];
    check(res, {
        'username value is theUser in get User': (r) => usernameValue === username,
    });

    // Negative Scenario
    performRequest('GET', '/user/theUser2', null, headers, {}, 404);

    sleep(timeSleep);
}

export function updateUser() {
    const jsonBody = JSON.stringify({
        "id": 10,
        "name": "doggie",
        "category": {
          "id": 1,
          "name": "Dogs"
        },
        "photoUrls": [
          "string"
        ],
        "tags": [
          {
            "id": 0,
            "name": "string"
          }
        ],
        "status": "available"
    });

    const headers = {
        'accept': '*/*',
        'Content-Type': 'application/json'
    };

    let res = performRequest('PUT', '/user/theUser', jsonBody, headers, {}, 200);
    let emailValue = JSON.parse(res.body).email;
    check(res, {
        'email value is john@email.com in update User': (r) => emailValue === 'john@email.com',
    });

    // Negative Scenarios
    performRequest('PUT', '/user/theUser22', jsonBody, headers, {}, 404);
    performRequest('PUT', '/user/theUser', 'null', headers, {}, 500);

    sleep(timeSleep);
}

export function deleteUser() {
    const headers = {
        'accept': '*/*'
    };

    let res = performRequest('DELETE', '/user/theUser', null, headers, {}, 200);
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    postUser();

    sleep(timeSleep);
}
