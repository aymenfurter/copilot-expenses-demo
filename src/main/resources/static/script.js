document.addEventListener('DOMContentLoaded', function() {
    toggleFormFieldsVisibility(true);
    const form = document.getElementById('claimForm');
    const fileInput = document.getElementById('fileUpload');
    const replyTextContainer = document.getElementById('replyTextContainer');
    const submitButton = form.querySelector('button[type="submit"]');
    const resetButton = form.querySelector('button[type="reset"]');
    const loader = document.createElement('div');
    loader.className = 'loader';
    loader.style.display = 'none';
    form.appendChild(loader);

    fileInput.addEventListener('change', function(event) {
        if (event.target.files.length) {
            handleSubmitFile(event.target.files[0]);
        }
    });

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        submitClaim();
    });

    resetButton.addEventListener('click', function() {
        resetForm();
    });
    
});

function handleSubmitFile(file) {
    const imagePreview = document.getElementById('imagePreview');
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    imagePreview.src = URL.createObjectURL(file);
    imagePreviewContainer.style.display = 'block';

    const formData = new FormData();
    formData.append('file', file);

    toggleLoader(true);


    var e = document.getElementById("expenseType");
    var expenseType = e.options[e.selectedIndex].value;

    document.getElementById('expenseType').style.setProperty('display', 'none', 'important');
    document.getElementById('expenseTypeLabel').style.setProperty('display', 'none', 'important');


    fetch('/api/image-analysis/upload', {
        method: 'POST',
        body: formData,
        headers: {
            'expenseType': expenseType
        }
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('intelligent-app-confirm').style.display = 'block';
        document.getElementById('confirmButton').style.display = 'block';
        document.getElementById('backButton').style.display = 'block';
        document.getElementById('expenseType').setAttribute('cssText', 'display:block !important');


        populateForm(data);
        displayReplyText(data.replyText);
    })
    .catch(error => console.error('Error:', error))
    .finally(() => toggleLoader(false));
}

function showExpenseTypes() {
    document.getElementById('expenseType').style.setProperty('display', 'block', 'important');
    document.getElementById('expenseTypeLabel').style.setProperty('display', 'block', 'important');
}

function populateForm(data) {
    document.getElementById('numberOfPeople').value = data.numberOfPeople || '';
    document.getElementById('numberOfPeopleReasoning').innerHTML = data.numberOfPeopleReasoning || '';
    document.getElementById('merchantName').value = data.merchantName || '';
    document.getElementById('geographicalLocation').value = data.geographicalLocation || '';
    document.getElementById('total').value = data.total || '';
    // not required: document.getElementById('businessJustification').value = data.businessJustification || '';
    document.getElementById('expenseType').value = data.expenseType || '';
    var dd = new Date().toLocaleDateString('en-GB');
    document.getElementById('claimDate').value = dd || '';


    enableFormFields();
}

function displayReplyText(text) {
    const replyText = document.getElementById('replyText');
    const replyTextContainer = document.getElementById('replyTextContainer');
    replyText.textContent = text;

    if (text) {
        replyTextContainer.style.display = 'flex';
    } else {
        replyTextContainer.style.display = 'none';
    }
}


function enableFormFields() {
    const fields = document.querySelectorAll('#claimForm input, #claimForm select, #claimForm textarea');
    fields.forEach(field => field.disabled = false);
}

function resetForm() {
    const imagePreviewContainer = document.getElementById('imagePreviewContainer');
    imagePreviewContainer.style.display = 'none';
    document.getElementById('imagePreview').src = '';
    const form = document.getElementById('claimForm');
    form.reset();
    enableFormFields();
    document.getElementById('replyText').textContent = '';
}

function submitClaim() {
    console.log('Claim submitted');
    var containerElement = document.querySelector('.container');
    containerElement.innerHTML = '<h1>Claim submitted</h1><p>Thank you for submitting your claim. We will get back to you as soon as possible.</p><button onclick="window.location.href=\'/\'">Submit another claim</button>'
}

function toggleLoader(show) {
    const loader = document.querySelector('.loader');
    loader.style.display = show ? 'block' : 'none';
}


function toggleFormFieldsVisibility(show) {
    document.getElementById('intelligent-app-confirm').style.display = 'none';
    const fields = document.querySelectorAll('#claimForm label, #claimForm input:not(#fileUpload), #claimForm select, #claimForm textarea');
    fields.forEach(field => {
        if (show) {
            field.classList.add('show-field');
            const submitButtons = document.querySelectorAll('button[type="submit"]');
            submitButtons.forEach(button => button.style.display = 'block');
            const resetButtons = document.querySelectorAll('button[type="reset"]');
            resetButtons.forEach(button => button.style.display = 'block')
        } else {
            field.classList.remove('show-field');
            const submitButtons = document.querySelectorAll('button[type="submit"]');
            submitButtons.forEach(button => button.style.display = 'none');
            const resetButtons = document.querySelectorAll('button[type="reset"]');
            resetButtons.forEach(button => button.style.display = 'none')
        }
    });
}

document.getElementById('intelligentApp').addEventListener('change', function(event) {
    if (event.target.checked) {
        document.getElementById('emoji-button').style.display = 'block';
        toggleFormFieldsVisibility(false);
    } else {
        document.getElementById('emoji-button').style.display = 'none';
        toggleFormFieldsVisibility(true);
    }
});