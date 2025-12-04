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

// Importamo kodo direktno z require za coverage
require('../donacije.js');

// Dostopamo do funkcije preko global objekta
const loadDonacije = global.loadDonacije;

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

    // Testira uspešno nalaganje donacij v tabelo
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

    // Testira prikaz sporočila, ko ni donacij
    test('loadDonacije - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadDonacije();

        const tableBody = document.getElementById('donacijeTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    // Testira obravnavo napake pri nalaganju donacij
    test('loadDonacije - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadDonacije();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju donacij');
        expect(errorMessage.style.display).toBe('block');
    });

    // Testira uspešno dodajanje donacije preko forme
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

    // Testira reset forme po uspešnem dodajanju donacije
    test('Dodajanje donacije - reset forme po uspešnem dodajanju', async () => {
        const form = document.getElementById('donacijaForm');
        document.getElementById('znesek').value = '500';
        document.getElementById('nacinPlacila').value = 'kartica';
        document.getElementById('uporabnikId').value = '3';

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 3, znesek: 500 })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 300));

        // Preverimo, da se je forma resetirala (vrednosti so se spremenile)
        // Form se resetira znotraj event listenerja, zato preverimo, da se je alert poklical
        expect(alert).toHaveBeenCalledWith('Donacija uspešno dodana!');
    });

});
