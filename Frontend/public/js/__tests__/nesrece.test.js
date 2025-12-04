// Mock fetch
global.fetch = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="nesreceTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
`;

const fs = require('fs');
const path = require('path');
const nesreceCode = fs.readFileSync(
    path.join(__dirname, '../nesrece.js'),
    'utf8'
);

eval(nesreceCode.replace('loadNesrece();', ''));

describe('Nesrece Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById('nesreceTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
    });

    // Testira uspešno nalaganje nesreč v tabelo
    test('loadNesrece - uspešno nalaganje nesreč', async () => {
        const mockNesrece = [
            { id: 1, opis: 'Prometna nesreča', lokacija: 'Ljubljana', datum: '2024-01-01', status: 'aktiven' },
            { id: 2, opis: 'Požar', lokacija: 'Maribor', datum: '2024-01-02', status: 'rešen' }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockNesrece
        });

        await loadNesrece();

        const tableBody = document.getElementById('nesreceTable');
        expect(tableBody.innerHTML).toContain('Prometna nesreča');
        expect(tableBody.innerHTML).toContain('Ljubljana');
        expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/nesrece');
    });

    // Testira prikaz sporočila, ko ni nesreč
    test('loadNesrece - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadNesrece();

        const tableBody = document.getElementById('nesreceTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    // Testira obravnavo napake pri nalaganju nesreč
    test('loadNesrece - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadNesrece();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju nesreč');
        expect(errorMessage.style.display).toBe('block');
    });

    // Testira prikaz N/A za manjkajoče podatke nesreč
    test('loadNesrece - obravnava manjkajočih podatkov', async () => {
        const mockNesrece = [
            { id: 1, opis: null, lokacija: null, datum: null, status: null }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockNesrece
        });

        await loadNesrece();

        const tableBody = document.getElementById('nesreceTable');
        expect(tableBody.innerHTML).toContain('N/A');
    });
});
