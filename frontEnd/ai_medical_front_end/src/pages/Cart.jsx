import { useEffect, useState } from 'react'
import api from '../utils/api'

export default function Cart() {
  const [cart, setCart] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  const fetchCart = () => {
    api.get('/api/cart')
      .then(res => { setCart(res.data); setLoading(false) })
      .catch(() => setLoading(false))
  }

  useEffect(() => { fetchCart() }, [])

  const removeItem = (medicineId) => {
    api.delete(`/api/cart/remove/${medicineId}`)
      .then(() => fetchCart())
  }

  const buyAll = () => {
    api.post('/api/cart/buy')
      .then(res => {
        setMessage(res.data)
        setCart([])
      })
      .catch(err => setMessage(err.response?.data?.message || 'Purchase failed'))
  }

  const total = cart.reduce((sum, item) => sum + item.quantity, 0)

  if (loading) return <div className="container"><p>Loading...</p></div>

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginBottom: '20px' }}>🛒 Cart ({cart.length} items)</h2>

        {message && (
          <p style={{ color: 'green', marginBottom: '15px', fontWeight: '600' }}>
            ✅ {message}
          </p>
        )}

        {cart.length === 0 ? (
          <p style={{ color: '#888' }}>Your cart is empty.</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Medicine</th>
                  <th>Quantity</th>
                  <th>Available Stock</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {cart.map(item => (
                  <tr key={item.medicineId}>
                    <td><strong>{item.medicineName}</strong></td>
                    <td>{item.quantity}</td>
                    <td>
                      <span className="badge badge-success">
                        {item.stockQuantity} in stock
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-danger"
                        onClick={() => removeItem(item.medicineId)}>
                        Remove
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <p><strong>Total Items: {total}</strong></p>
              <button className="btn btn-success" onClick={buyAll}>
                Buy All 🛍️
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  )
}