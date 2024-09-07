import { check, sleep } from 'k6';
import { performRequest } from './CommonFuction.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const timeSleep = 1;

export function handleSummary(data) {
    return {
      "StoreTest.html": htmlReport(data),
    };
  }

export const options = {
    scenarios: generateScenarios([
        'getInventory',
        'postOrder',
        'getOrder',
        'deleteOrder'
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

export function getInventory() {
    const headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    };
    
    let res = performRequest('GET', '/store/inventory', null, headers, {}, 200);
    let approvedValue = JSON.parse(res.body).approved;
    check(res, {
        'approved value is 57': (r) => approvedValue === 57,
    });

    sleep(timeSleep);
}

export function postOrder() {
    const jsonBody = JSON.stringify({
        "id": 10,
        "petId": 198772,
        "quantity": 7,
        "shipDate": "2024-09-06T14:32:33.847Z",
        "status": "approved",
        "complete": true
      });

    const headers = {
        'accept': 'application/json',
        'Content-Type': 'application/json'
    };

    let res = performRequest('POST', '/store/order', jsonBody, headers, {}, 200);
    let statusValue = JSON.parse(res.body).status;
    check(res, {
        'status value is approved': (r) => statusValue === 'approved',
    });

    // Negative Scenarios
    let invalidJsonBody = JSON.stringify({ ...JSON.parse(jsonBody), shipDate: true });
    performRequest('POST', '/store/order', invalidJsonBody, headers, {}, 400);
    performRequest('POST', '/store/order', '', headers, {}, 400);

    sleep(timeSleep);
}

export function getOrder() {
    const headers = {
        'accept': 'application/xml'
    };

    let res = performRequest('GET', '/store/order/2', null, headers, {}, 200);
    let xml = res.body;
    let idValue = xml.match(/<id>(\d+)<\/id>/)[1];
    check(res, {
        'id value is 2': (r) => idValue === '2',
    });

    // Negative Scenarios
    performRequest('GET', '/store/order/2222', null, headers, {}, 404);
    performRequest('GET', '/store/order/2.6', null, headers, {}, 400);

    sleep(timeSleep);
}

export function deleteOrder() {
    const headers = {
        'accept': '*/*'
    };

    let res = performRequest('DELETE', '/store/order/10', null, headers, {}, 200);
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(timeSleep);
}
