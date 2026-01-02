const API_BASE_URL = 'http://localhost:8080/api/v1';

async function loadOskodovanci() {
    try {
        const response = await fetch(`${API_BASE_URL}/oskodovanci`);
        const oskodovanci = await response.json();
        
        const tbody = document.getElementById('oskodovanciTable');
        
        if (oskodovanci.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Ni podatkov</td></tr>';
            return;
        }
        
        tbody.innerHTML = oskodovanci.map(o => `
            <tr>
                <td>${o.id}</td>
                <td>${o.ime || 'N/A'}</td>
                <td>${o.priimek || 'N/A'}</td>
                <td>${o.naslov || 'N/A'}</td>
                <td>${o.telefon || 'N/A'}</td>
            </tr>
        `).join('');
        
    } catch (error) {
        document.getElementById('errorMessage').textContent = 'Napaka pri pridobivanju oškodovancev';
        document.getElementById('errorMessage').style.display = 'block';
        document.getElementById('oskodovanciTable').innerHTML = '<tr><td colspan="5" style="text-align: center;">Napaka pri nalaganju</td></tr>';
    }
}

// Auto-load samo če ni v test okolju
if (typeof jest === 'undefined') {
    loadOskodovanci();
}
