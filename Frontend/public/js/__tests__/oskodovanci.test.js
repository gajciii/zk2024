// Mock fetch
global.fetch = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="oskodovanciTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
`;

const loadOskodovanci = async () => {
    try {
        const response = await fetch('http://localhost:8080/api/v1/oskodovanci');
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
};

describe('Oskodovanci Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById('oskodovanciTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
    });

    test('loadOskodovanci - uspešno nalaganje oškodovancev', async () => {
        const mockOskodovanci = [
            { id: 1, ime: 'Janez', priimek: 'Novak', naslov: 'Ljubljana', telefon: '123456789' },
            { id: 2, ime: 'Miha', priimek: 'Horvat', naslov: 'Maribor', telefon: '987654321' }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockOskodovanci
        });

        await loadOskodovanci();

        const tableBody = document.getElementById('oskodovanciTable');
        expect(tableBody.innerHTML).toContain('Janez');
        expect(tableBody.innerHTML).toContain('Novak');
        expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/oskodovanci');
    });

    test('loadOskodovanci - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadOskodovanci();

        const tableBody = document.getElementById('oskodovanciTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    test('loadOskodovanci - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadOskodovanci();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju oškodovancev');
        expect(errorMessage.style.display).toBe('block');
    });

    test('loadOskodovanci - obravnava manjkajočih podatkov', async () => {
        const mockOskodovanci = [
            { id: 1, ime: null, priimek: null, naslov: null, telefon: null }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockOskodovanci
        });

        await loadOskodovanci();

        const tableBody = document.getElementById('oskodovanciTable');
        expect(tableBody.innerHTML).toContain('N/A');
    });
});

