import { useEffect, useState } from 'react'
import api from '../utils/api'

const styles = {
  container: { padding: '1.5rem', maxWidth: '1100px', margin: '0 auto' },
  loading: { color: '#888', fontSize: '15px', textAlign: 'center', padding: '3rem 0' },
  toast: { background: '#EAF3DE', color: '#3B6D11', border: '0.5px solid #C0DD97', padding: '10px 16px', borderRadius: '10px', marginBottom: '1.2rem', fontSize: '14px', fontWeight: 500 },
  header: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.4rem' },
  title: { fontSize: '20px', fontWeight: 500, color: '#1a1a1a' },
  countBadge: { fontSize: '12px', padding: '3px 10px', borderRadius: '20px', background: '#f3f3f0', color: '#777', border: '0.5px solid #ddd' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '14px' },
  card: { background: '#fff', border: '0.5px solid #e0e0dd', borderRadius: '12px', padding: '1.1rem 1.2rem', display: 'flex', flexDirection: 'column', gap: '10px', transition: 'border-color 0.2s, box-shadow 0.2s', cursor: 'default' },
  cardTop: { display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '8px' },
  icon: { width: '36px', height: '36px', borderRadius: '8px', background: '#E1F5EE', color: '#1D9E75', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 },
  name: { fontSize: '15px', fontWeight: 500, color: '#1a1a1a' },
  divider: { border: 'none', borderTop: '0.5px solid #e5e5e2' },
  meta: { display: 'flex', flexDirection: 'column', gap: '5px' },
  metaRow: { display: 'flex', gap: '6px', fontSize: '13px' },
  metaLabel: { color: '#888', minWidth: '58px' },
  metaVal: { color: '#333' },
  actions: { display: 'flex', gap: '8px', flexWrap: 'wrap' },
  badgeSuccess: { fontSize: '11px', padding: '2px 9px', borderRadius: '20px', fontWeight: 500, whiteSpace: 'nowrap', background: '#EAF3DE', color: '#3B6D11' },
  badgeDanger: { fontSize: '11px', padding: '2px 9px', borderRadius: '20px', fontWeight: 500, whiteSpace: 'nowrap', background: '#FCEBEB', color: '#A32D2D' },
  btnAdd: { fontSize: '13px', padding: '6px 13px', borderRadius: '8px', border: '0.5px solid #1D9E75', background: 'transparent', cursor: 'pointer', color: '#0F6E56', fontFamily: 'inherit' },
  btnDel: { fontSize: '13px', padding: '6px 13px', borderRadius: '8px', border: '0.5px solid #E24B4A', background: 'transparent', cursor: 'pointer', color: '#A32D2D', fontFamily: 'inherit' },
}

const MedicineIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <rect x="3" y="3" width="18" height="18" rx="4"/>
    <path d="M3 12h18M12 3v18"/>
  </svg>
)

export default function MedicineList() {
  const [medicines, setMedicines] = useState([])
  const [loading, setLoading] = useState(true)
  const [cartMsg, setCartMsg] = useState('')
  const [hoveredId, setHoveredId] = useState(null)
  const role = localStorage.getItem('role')

  useEffect(() => {
    api.get('/api/medicine/all')
      .then(res => { setMedicines(res.data); setLoading(false) })
      .catch(() => setLoading(false))
  }, [])

  const deleteMedicine = (id) => {
    if (!window.confirm('Delete this medicine?')) return
    api.delete(`/api/medicine/delete/${id}`)
      .then(() => setMedicines(medicines.filter(m => m.id !== id)))
  }

  const addToCart = (id) => {
    api.post(`/api/cart/add/${id}`)
      .then(res => {
        setCartMsg(res.data)
        setTimeout(() => setCartMsg(''), 2000)
      })
      .catch(err => {
        setCartMsg(err.response?.data?.message || 'Failed to add')
        setTimeout(() => setCartMsg(''), 2000)
      })
  }

  if (loading) return (
    <div style={styles.container}>
      <p style={styles.loading}>Loading medicines...</p>
    </div>
  )

  return (
    <div style={styles.container}>

      {cartMsg && <div style={styles.toast}>{cartMsg}</div>}

      <div style={styles.header}>
        <h2 style={styles.title}>All medicines</h2>
        <span style={styles.countBadge}>{medicines.length} items</span>
      </div>

      <div style={styles.grid}>
        {medicines.map(m => (
          <div
            key={m.id}
            style={{
              ...styles.card,
              ...(hoveredId === m.id ? { borderColor: '#bbb', boxShadow: '0 2px 10px rgba(0,0,0,0.06)' } : {})
            }}
            onMouseEnter={() => setHoveredId(m.id)}
            onMouseLeave={() => setHoveredId(null)}
          >
            <div style={styles.cardTop}>
              <div style={styles.icon}><MedicineIcon /></div>
              <span style={m.stockQuantity > 0 ? styles.badgeSuccess : styles.badgeDanger}>
                {m.stockQuantity > 0 ? `${m.stockQuantity} in stock` : 'Out of stock'}
              </span>
            </div>

            <p style={styles.name}>{m.name}</p>

            <hr style={styles.divider} />

            <div style={styles.meta}>
              <div style={styles.metaRow}>
                <span style={styles.metaLabel}>Used for</span>
                <span style={styles.metaVal}>{m.usedFor}</span>
              </div>
              <div style={styles.metaRow}>
                <span style={styles.metaLabel}>Dosage</span>
                <span style={styles.metaVal}>{m.dosage}</span>
              </div>
            </div>

            <hr style={styles.divider} />

            <div style={styles.actions}>
              {m.stockQuantity > 0 && (
                <button
                  style={styles.btnAdd}
                  onMouseEnter={e => e.target.style.background = '#E1F5EE'}
                  onMouseLeave={e => e.target.style.background = 'transparent'}
                  onClick={() => addToCart(m.id)}
                >
                  + Add to cart
                </button>
              )}
              {role === 'ADMIN' && (
                <button
                  style={styles.btnDel}
                  onMouseEnter={e => e.target.style.background = '#FCEBEB'}
                  onMouseLeave={e => e.target.style.background = 'transparent'}
                  onClick={() => deleteMedicine(m.id)}
                >
                  Delete
                </button>
              )}
            </div>
          </div>
        ))}
      </div>

    </div>
  )
}