import { useState } from 'react'
import axios from 'axios'

export default function AddMedicine() {
    const [form, setForm] = useState({
        name: '', usedFor: '', dosage: '',
        sideEffects: '', warnings: '', stockQuantity: ''
    })
    const [message, setMessage] = useState('')

    const handleSubmit = () => {
        axios.post('http://localhost:8080/api/medicine/add', {
            ...form, stockQuantity: parseInt(form.stockQuantity)
        })
            .then(res => { setMessage(res.data); setForm({ name: '', usedFor: '', dosage: '', sideEffects: '', warnings: '', stockQuantity: '' }) })
            .catch(() => setMessage('Error adding medicine'))
    }

    return (
        <div className="container">
            <div className="card">
                <h2 style={{ marginBottom: '20px' }}>Add New Medicine</h2>
                {message && <p style={{ color: 'green', marginBottom: '15px' }}>{message}</p>}
                <input placeholder="Medicine Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                <input placeholder="Used For" value={form.usedFor} onChange={e => setForm({ ...form, usedFor: e.target.value })} />
                <input placeholder="Dosage" value={form.dosage} onChange={e => setForm({ ...form, dosage: e.target.value })} />
                <input placeholder="Side Effects" value={form.sideEffects} onChange={e => setForm({ ...form, sideEffects: e.target.value })} />
                <input placeholder="Warnings" value={form.warnings} onChange={e => setForm({ ...form, warnings: e.target.value })} />
                <input placeholder="Stock Quantity" type="number" value={form.stockQuantity} onChange={e => setForm({ ...form, stockQuantity: e.target.value })} />
                <button className="btn btn-primary" onClick={handleSubmit}>Add Medicine</button>
            </div>
        </div>
    )
}