import { useEffect, useState } from 'react'
import axios from 'axios'

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    axios.get('http://localhost:8080/api/medicine/orders')
      .then(res => { setOrders(res.data); setLoading(false) })
      .catch(() => setLoading(false))
  }, [])

  if (loading) return <div className="container"><p>Loading...</p></div>

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginBottom: '20px' }}>Orders ({orders.length})</h2>
        {orders.length === 0
          ? <p style={{ color: '#888' }}>No orders yet.</p>
          : <table>
              <thead>
                <tr>
                  <th>Medicine</th>
                  <th>Quantity</th>
                  <th>Status</th>
                  <th>Order Time</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(o => (
                  <tr key={o.id}>
                    <td><strong>{o.medicineName}</strong></td>
                    <td>{o.quantity}</td>
                    <td>
                      <span className={`badge ${o.status === 'CONFIRMED' ? 'badge-success' : 'badge-warning'}`}>
                        {o.status}
                      </span>
                    </td>
                    <td>{new Date(o.orderTime).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
        }
      </div>
    </div>
  )
}