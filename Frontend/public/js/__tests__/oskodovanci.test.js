// Mock fetch
global.fetch = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="oskodovanciTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
`;

// Importamo kodo direktno z require za coverage
require('../oskodovanci.js');

// Dostopamo do funkcije preko global objekta
const loadOskodovanci = global.loadOskodovanci;

describe('Oskodovanci Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById('oskodovanciTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
    });

    // Testira uspešno nalaganje oškodovancev v tabelo
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

    // Testira prikaz sporočila, ko ni oškodovancev
    test('loadOskodovanci - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadOskodovanci();

        const tableBody = document.getElementById('oskodovanciTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    // Testira obravnavo napake pri nalaganju oškodovancev
    test('loadOskodovanci - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadOskodovanci();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju oškodovancev');
        expect(errorMessage.style.display).toBe('block');
    });

    // Testira prikaz N/A za manjkajoče podatke oškodovancev
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
