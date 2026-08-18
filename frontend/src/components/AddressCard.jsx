import { useNavigate } from 'react-router'
import { EllipsisVertical, SquarePen, Trash2 } from 'lucide-react'
import styles from '../styles/ManageAddresses.module.css'
import { useContext } from 'react'
import AddressesContext from '../contexts/AddressesContext'

import { useMutation, useQueryClient } from '@tanstack/react-query'
import { api } from '@/api/client'

export default function AddressCard({ address, threeDotsMenuOpenId, setThreeDotsMenuOpenId, desktopSelectStyles, setShowAddressSelector }) {

    const { selectedAddressId, setSelectedAddressId } = useContext(AddressesContext)
    const { id, name, addressLine1, addressLine2, pincode, city, state, mobileNumber, addressType, defaultShipping } = address
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const { mutate: deleteAddress } = useMutation({
        mutationFn: (addressId) => api.delete(`/addresses/${addressId}`),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ['addresses', 'list', id] })
    })

    const { mutate: changeDefaultAddress } = useMutation({
        mutationFn: (addressId) => api.patch(`/addresses/${addressId}/default`),
        onSuccess: (defaultAddress) => queryClient.setQueryData(['addresses', 'detail', 'default'], defaultAddress)
    })

    return (
        <div className={`${styles.addressCard} ${desktopSelectStyles?.addressCard}`} id={id === selectedAddressId ? styles.selected : ''} onClick={selectedAddressId ? e => {
            document.getElementById(styles.selected).removeAttribute('id')
            e.currentTarget.id = styles.selected
            setSelectedAddressId(address.id)
        } : undefined}>
            <div className={styles.addressSubcard}>
                {selectedAddressId && <div className={styles.customRadio}></div>}
                <div className={styles.address}>
                    {defaultShipping && <><div className={styles.defaultBadge}>Default</div><br /></>}
                    <span className={styles.name}>{name}</span> <span className={styles.addressType}>{addressType}</span><br />
                    <span className={styles.addressLine1}>{addressLine1}</span>, <span className={styles.addressLine2}>{addressLine2}</span>, <span className={styles.city}>{city}</span>, <span className={styles.state}>{state}</span> - <span className={styles.pincode}>{pincode}</span><br />
                    Phone: <span className={styles.mobileNumber}>{mobileNumber}</span>
                </div>
                <div className={`${styles.threeDotsMenu} ${threeDotsMenuOpenId === id || selectedAddressId === id ? styles.open : ''}`}>
                    {!selectedAddressId && <EllipsisVertical className={styles.threeDots} onClick={e => {
                        e.stopPropagation()
                        setThreeDotsMenuOpenId(id)
                    }} />}
                    <ul className={styles.options}>
                        <li className={styles.edit} onClick={() => {
                            navigate(`/addresses/${address.id}/edit`)
                        }}><SquarePen />Edit</li>
                        {!selectedAddressId && !defaultShipping && <li className={styles.delete} onClick={() => deleteAddress(id)}><Trash2 /> Delete</li>}
                        {!selectedAddressId && !defaultShipping && <li className={styles.setDefault} onClick={() => changeDefaultAddress(id)}>Set as default</li>}
                    </ul>
                </div>
            </div>
            {selectedAddressId && id === selectedAddressId && <button className={styles.btnDeliverAddress} onClick={setShowAddressSelector ? () => setShowAddressSelector(false) : () => navigate('/checkout')}>Deliver to this address</button>}
        </div>
    )
}