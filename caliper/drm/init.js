/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

module.exports.info  = 'Creating sectors.';

let txIndex = 0;
//let sectors = ['0', '1', '2', '3', '4', '5', '6', '8', '9'];
let prices = ['100', '200', '300', '400'];
let bc, contx;
module.exports.init = function(blockchain, context, args) {
    bc = blockchain;
    contx = context;
    return Promise.resolve();
};

module.exports.run = function() {
    txIndex++;
    return bc.invokeSmartContract(contx, 'price_chaincode_go', 'v1',
        {
            verb: 'initPrice',
            sector: txIndex,
            price: prices[txIndex % prices.length]
        }, 30);
};

module.exports.end = function(results) {
    return Promise.resolve();
};
