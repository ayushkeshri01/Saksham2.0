const crypto = require('crypto');

const EASEBUZZ_API_BASE = process.env.EASEBUZZ_API_BASE || 'https://wire.easebuzz.in/api/v1';
const EASEBUZZ_WIRE_KEY = process.env.EASEBUZZ_WIRE_KEY || '';
const EASEBUZZ_WIRE_SALT = process.env.EASEBUZZ_WIRE_SALT || '';
const EASEBUZZ_VIRTUAL_ACCOUNT = process.env.EASEBUZZ_VIRTUAL_ACCOUNT || '';

function isConfigured() {
    return Boolean(EASEBUZZ_WIRE_KEY && EASEBUZZ_WIRE_SALT);
}

function ensureConfigured() {
    if (!isConfigured()) {
        throw new Error('Easebuzz payout credentials are not configured. Set EASEBUZZ_WIRE_KEY and EASEBUZZ_WIRE_SALT in backend/.env');
    }
}

// Easebuzz request hash: SHA-512(key | field1 | field2 | ... | fieldN | salt)
function buildAuthorizationHash({ key, salt, fields }) {
    const payload = [key, ...fields, salt].join('|');
    return crypto.createHash('sha512').update(payload, 'utf8').digest('hex');
}

// Easebuzz sends whole amounts as integers and decimal amounts with as many decimals as given
function formatAmount(value) {
    const n = Number(value);
    if (!Number.isFinite(n)) throw new Error('Invalid amount');
    return String(n);
}

async function request(path, { hashKeys, body = {} }) {
    ensureConfigured();
    const payload = { ...body, key: EASEBUZZ_WIRE_KEY };
    const fields = hashKeys.map((k) => {
        const v = payload[k];
        return v === undefined || v === null ? '' : String(v);
    });

    const authorization = buildAuthorizationHash({
        key: EASEBUZZ_WIRE_KEY,
        salt: EASEBUZZ_WIRE_SALT,
        fields,
    });

    const url = `${EASEBUZZ_API_BASE}${path}`;

    const res = await fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'WIRE-API-KEY': EASEBUZZ_WIRE_KEY,
            'Authorization': authorization,
        },
        body: JSON.stringify(payload),
    });

    const text = await res.text();
    let json;
    try {
        json = JSON.parse(text);
    } catch (err) {
        throw new Error(`Easebuzz returned non-JSON response (${res.status}): ${text}`);
    }

    return {
        httpStatus: res.status,
        success: json.success === true,
        message: json.message || null,
        data: json.data || null,
    };
}

// Create a contact (payer/beneficiary holder) in Easebuzz Wire
async function createContact({ name, email = '', phone = '' }) {
    return request('/contacts/', {
        hashKeys: ['name', 'email', 'phone'],
        body: { name, email, phone },
    });
}

// Register a beneficiary (bank account or UPI handle) against a contact
async function createBeneficiary({
    contact_id,
    beneficiary_type,
    beneficiary_name,
    account_number = '',
    ifsc = '',
    upi_handle = '',
}) {
    return request('/beneficiaries/', {
        hashKeys: ['contact_id', 'beneficiary_name', 'account_number', 'ifsc', 'upi_handle'],
        body: {
            contact_id,
            beneficiary_type,
            beneficiary_name,
            account_number,
            ifsc,
            upi_handle,
        },
    });
}

// Initiate a payout transfer to a registered beneficiary
async function initiateTransfer({
    beneficiary_code,
    unique_request_number,
    amount,
    payment_mode = 'IMPS',
    narration = '',
    virtual_account_number = EASEBUZZ_VIRTUAL_ACCOUNT,
    udf1 = '',
    udf2 = '',
    udf3 = '',
    udf4 = '',
    udf5 = '',
    scheduled_for = '',
}) {
    if (!beneficiary_code) {
        throw new Error('beneficiary_code is required to initiate an Easebuzz transfer.');
    }

    const amountStr = formatAmount(amount);

    const body = {
        beneficiary_code,
        unique_request_number,
        payment_mode,
        amount: amountStr,
    };
    if (virtual_account_number) body.virtual_account_number = virtual_account_number;
    if (narration) body.narration = narration;
    if (scheduled_for) body.scheduled_for = scheduled_for;
    if (udf1) body.udf1 = udf1;
    if (udf2) body.udf2 = udf2;
    if (udf3) body.udf3 = udf3;
    if (udf4) body.udf4 = udf4;
    if (udf5) body.udf5 = udf5;

    return request('/transfers/initiate/', {
        hashKeys: ['beneficiary_code', 'unique_request_number', 'amount'],
        body,
    });
}

module.exports = {
    EASEBUZZ_API_BASE,
    EASEBUZZ_WIRE_KEY,
    EASEBUZZ_WIRE_SALT,
    EASEBUZZ_VIRTUAL_ACCOUNT,
    isConfigured,
    formatAmount,
    buildAuthorizationHash,
    createContact,
    createBeneficiary,
    initiateTransfer,
};