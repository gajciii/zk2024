// Mock fetch
global.fetch = jest.fn();
global.alert = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="donacijeTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
    <form id="donacijaForm">
        <input id="znesek" value="100">
        <input id="nacinPlacila" value="kartica">
        <input id="uporabnikId" value="1">
    </form>
`;

const fs = require('fs');
const path = require('path');
const donacijeCode = fs.readFileSync(
    path.join(__dirname, '../donacije.js'),
    'utf8'
);

eval(donacijeCode.replace('loadDonacije();', ''));

describe('Donacije Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        alert.mockClear();
        document.getElementById('donacijeTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
        document.getElementById('znesek').value = '100';
        document.getElementById('nacinPlacila').value = 'kartica';
        document.getElementById('uporabnikId').value = '1';
    });

    test('loadDonacije - uspešno nalaganje donacij', async () => {
        const mockDonacije = [
            { id: 1, znesek: 100, datumDonacije: '2024-01-01', nacinPlacila: 'kartica', status: 'potrjena' },
            { id: 2, znesek: 200, datumDonacije: '2024-01-02', nacinPlacila: 'gotovina', status: 'potrjena' }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockDonacije
        });

        await loadDonacije();

        const tableBody = document.getElementById('donacijeTable');
        expect(tableBody.innerHTML).toContain('100');
        expect(tableBody.innerHTML).toContain('kartica');
        expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/uporabniki/donacije');
    });

    test('loadDonacije - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadDonacije();

        const tableBody = document.getElementById('donacijeTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    test('loadDonacije - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadDonacije();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju donacij');
        expect(errorMessage.style.display).toBe('block');
    });

    test('Dodajanje donacije - uspešno', async () => {
        const form = document.getElementById('donacijaForm');

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 1, znesek: 100 })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 200));

        expect(fetch).toHaveBeenCalled();
        expect(alert).toHaveBeenCalledWith('Donacija uspešno dodana!');
    });

    test('Dodajanje donacije - napaka', async () => {
        const form = document.getElementById('donacijaForm');

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Napaka' })
        });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 200));

        expect(fetch).toHaveBeenCalled();
        const alertCalls = alert.mock.calls;
        const hasErrorAlert = alertCalls.some(call => call[0] === 'Napaka pri dodajanju donacije');
        expect(hasErrorAlert).toBe(true);
    });
});
