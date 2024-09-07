import { check, sleep } from 'k6';
import { performRequest } from './CommonFuction.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const timeSleep = 1;

export function handleSummary(data) {
    return {
      "PetTest.html": htmlReport(data),
    };
  }


export const options = {
    scenarios: generateScenarios([
        'putPet',
        'getPetsByStatus',
        'getPetsByTags',
        'getPetById',
        'postPet',
        'postPetWithParams',
        // 'uploadPetImage',
        'deletePet'
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

export function putPet() {
    const jsonBody = JSON.stringify({
        "id": 10,
        "category": { "id": 1, "name": "Dogs" },
        "name": "doggie",
        "photoUrls": ["string"],
        "tags": [{ "id": 0, "name": "string" }],
        "status": "available"
    });
    const headers = { 'Accept': 'application/xml', 'Content-Type': 'application/json' };

    let res = performRequest('PUT', '/pet', jsonBody, headers, {}, 200);
    check(res, { 'category name is Dogs': (r) => r.body.includes('<name>Dogs</name>') });

    sleep(timeSleep);
}

export function getPetsByStatus() {
    const url = '/pet/findByStatus?status=available';
    const headers = { 'Accept': 'application/xml' };
    let res = performRequest('GET', url, null, headers, {}, 200);
    check(res, { 'status value is available': (r) => r.body.includes('<status>available</status>') });

    sleep(timeSleep);
}

export function getPetsByTags() {
    const url = '/pet/findByTags?tags=string';
    const headers = { 'Accept': 'application/xml' };
    let res = performRequest('GET', url, null, headers, {}, 200);
    check(res, { 'tag name is string': (r) => r.body.includes('<name>string</name>') });

    sleep(timeSleep);
}

export function getPetById() {
    const petID = '10';
    const url = `/pet/${petID}`;
    const headers = { 'Accept': 'application/xml' };
    let res = performRequest('GET', url, null, headers, {}, 200);
    check(res, { 'pet ID value is 10 in Get Pet By ID': (r) => r.body.includes('<id>10</id>') });

    sleep(timeSleep);
}

export function postPet() {
    const jsonBody = JSON.stringify({
        "id": 10,
        "category": { "id": 1, "name": "Dogs" },
        "name": "doggie",
        "photoUrls": ["string"],
        "tags": [{ "id": 0, "name": "string" }],
        "status": "available"
    });
    const headers = { 'Accept': 'application/xml', 'Content-Type': 'application/json' };

    let res = performRequest('POST', '/pet', jsonBody, headers, {}, 200);
    check(res, { 'pet ID value is 10 in post Pet': (r) => r.body.includes('<id>10</id>') });

    sleep(timeSleep);
}

export function postPetWithParams() {
    const petID = 10;
    const url = `/pet/${petID}?name=string&status=pending`;
    const headers = { 'Accept': '*/*' };
    let res = performRequest('POST', url, '', headers, {}, 200);
    check(res, { 'pet ID value is 10 in Post Params': (r) => r.json().id === petID });

    sleep(timeSleep);
}

/*export function uploadPetImage() {
    const file = readFile('src/test/resources/files/QAAT_Challenge_YP.jpg');
    const url = '/pet/2/uploadImage?additionalMetadata=test';
    const headers = { 'Accept': 'application/json', 'Content-Type': 'application/octet-stream' };

    let res = http.post(`${BASE_URL}${url}`, file, { headers: headers });
    check(res, { 'status is 200': (r) => r.status === 200 });

    sleep(timeSleep);
}*/

export function deletePet() {
    const url = '/pet/10';
    const headers = { 'Accept': '*/*' };
    let res = performRequest('DELETE', url, null, headers, {}, 200);
    check(res, { 'status is 200': (r) => r.status === 200 });

    postPet();
    sleep(timeSleep);
}
