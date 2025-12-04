// Mock fetch
global.fetch = jest.fn();

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

const loadDonacije = async () => {
    try {
        const response = await fetch('http://localhost:8080/api/v1/uporabniki/donacije');
        const donacije = await response.json();
        
        const tbody = document.getElementById('donacijeTable');
        
        if (donacije.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Ni podatkov</td></tr>';
            return;
        }
        
        tbody.innerHTML = donacije.map(d => `
            <tr>
                <td>${d.id}</td>
                <td>${d.znesek} €</td>
                <td>${d.datumDonacije || 'N/A'}</td>
                <td>${d.nacinPlacila || 'N/A'}</td>
                <td>${d.status || 'N/A'}</td>
            </tr>
        `).join('');
        
    } catch (error) {
        document.getElementById('errorMessage').textContent = 'Napaka pri pridobivanju donacij';
        document.getElementById('errorMessage').style.display = 'block';
        document.getElementById('donacijeTable').innerHTML = '<tr><td colspan="5" style="text-align: center;">Napaka pri nalaganju</td></tr>';
    }
};

describe('Donacije Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById('donacijeTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
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
        
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, znesek: 100 })
        });

        const event = new Event('submit');
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 100));

        expect(fetch).toHaveBeenCalled();
    });
});

