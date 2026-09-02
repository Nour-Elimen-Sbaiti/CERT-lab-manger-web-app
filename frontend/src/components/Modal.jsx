import React from 'react'
import { IconButton } from './Icons'

export function Modal({ title, onClose, children, wide }) {
  return (
    <div className="modal-backdrop" onMouseDown={onClose}>
      <section className={`modal ${wide ? 'wide-modal' : ''}`} onMouseDown={e => e.stopPropagation()}>
        <div className="modal-title">
          <h2>{title}</h2>
          <IconButton title="Fermer" onClick={onClose}>×</IconButton>
        </div>
        {children}
      </section>
    </div>
  )
}
