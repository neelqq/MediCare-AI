import { useState } from 'react'
import api from '../utils/api'

export default function Suggest() {
  const [problem, setProblem] = useState('')
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [orderMsg, setOrderMsg] = useState('')
  const [error, setError] = useState('')

  const handleSuggest = () => {
    if (!problem) return
    setLoading(true)
    setResult(null)
    setOrderMsg('')
    setError('')

    api.get(`/api/medicine/suggest/check?problem=${problem}`)
      .then(res => { setResult(res.data); setLoading(false) })
      .catch(err => {
        const status = err.response?.data?.status
        const message = err.response?.data?.message

        if (status === 409) {
          setResult({
            Status: 'OUT OF STOCK',
            'Medicine Name': message?.replace('Medicine is out of stock: ', ''),
            Message: 'This medicine is not available. Would you like to place an order?'
          })
        } else if (status === 404) {
          setError('No medicine found for this problem.')
        } else {
          setError('Something went wrong. Please try again.')
        }
        setLoading(false)
      })
  }

  const handleOrder = () => {
    api.post(`/api/medicine/order?medicineName=${result['Medicine Name']}&quantity=1`)
      .then(res => setOrderMsg(res.data))
      .catch(() => setOrderMsg('Failed to place order. Please try again.'))
  }

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginBottom: '20px' }}>AI Medicine Suggest</h2>
        <input
          placeholder="Describe your problem... (e.g. fever and headache)"
          value={problem}
          onChange={e => setProblem(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSuggest()}
        />
        <button className="btn btn-primary" onClick={handleSuggest} disabled={loading}>
          {loading ? 'Finding...' : 'Get Suggestion'}
        </button>
      </div>

      {error && (
        <div className="card" style={{ borderLeft: '4px solid #e74c3c' }}>
          <p style={{ color: '#e74c3c' }}>❌ {error}</p>
        </div>
      )}

      {result && (
        <div className="card">
          {result.Status === 'OUT OF STOCK' ? (
            <>
              <h3 style={{ color: '#e74c3c', marginBottom: '15px' }}>⚠️ Out of Stock</h3>
              <p><strong>Medicine:</strong> {result['Medicine Name']}</p>
              <p style={{ margin: '10px 0' }}>{result.Message}</p>
              {orderMsg
                ? <p style={{ color: 'green', marginTop: '10px' }}>✅ {orderMsg}</p>
                : <button className="btn btn-success" style={{ marginTop: '10px' }} onClick={handleOrder}>
                    Place Order
                  </button>
              }
            </>
          ) : (
            <>
              <h3 style={{ color: '#2c7be5', marginBottom: '15px' }}>💊 {result['Medicine Name']}</h3>
              <p><strong>About:</strong> {result['About']}</p>
              <p style={{ margin: '8px 0' }}><strong>Who Can Use:</strong> {result['Who Can Use']}</p>
              <p style={{ margin: '8px 0' }}><strong>Who Cannot Use:</strong> {result['Who Cannot Use']}</p>
              <p style={{ margin: '8px 0' }}>
                <strong>Stock: </strong>
                <span className="badge badge-success">{result['Stock Available']} available</span>
              </p>
              <p style={{ marginTop: '15px', color: '#888', fontSize: '13px' }}>
                ⚕️ {result["Doctor's Note"]}
              </p>
            </>
          )}
        </div>
      )}
    </div>
  )
}