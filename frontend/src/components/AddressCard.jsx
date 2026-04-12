import { useNavigate } from 'react-router'
import { EllipsisVertical, SquarePen } from 'lucide-react'
import styles from '../styles/ManageAddresses.module.css'
import { useContext } from 'react'
import AddressesContext from '../contexts/AddressesContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { useMutation, useQueryClient } from '@tanstack/react-query'

async function apiDeleteAddress(authFetch, addressId) {
    await authFetch(`${apiBaseUrl}/api/addresses/${addressId}`, { method: 'DELETE' })
}

async function apiChangeDefaultAddress(authFetch, addressId) {
    const res = await authFetch(`${apiBaseUrl}/api/addresses/${addressId}/default`, { method: 'PATCH' })
    return res.json()
}

export default function AddressCard({ address, threeDotsMenuOpenId, setThreeDotsMenuOpenId, desktopSelectStyles, setShowAddressSelector }) {

    const { addresses, selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const { id, name, addressLine1, addressLine2, pincode, city, state, mobileNumber, addressType, defaultShipping } = address
    const navigate = useNavigate()
    const authFetch = useAuthFetch()
    const queryClient = useQueryClient()

    const { mutate: deleteAddress } = useMutation({
        mutationFn: (addressId) => apiDeleteAddress(authFetch, addressId),
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ['addresses', 'list', id] })
    })

    const { mutate: changeDefaultAddress } = useMutation({
        mutationFn: (addressId) => apiChangeDefaultAddress(authFetch, addressId),
        onSuccess: (defaultAddress) => queryClient.setQueryData(['addresses', 'detail', 'default'], defaultAddress)
    })

    return (
        <div className={`${styles.addressCard} ${desktopSelectStyles?.addressCard}`} id={id === selectedAddress?.id ? styles.selected : ''} onClick={selectedAddress ? e => {
            document.getElementById(styles.selected).removeAttribute('id')
            e.currentTarget.id = styles.selected
            setSelectedAddress(address)
        } : undefined}>
            <div className={styles.addressSubcard}>
                {selectedAddress && <div className={styles.customRadio}></div>}
                <div className={styles.address}>
                    {defaultShipping && <><div className={styles.defaultBadge}>Default</div><br /></>}
                    <span className={styles.name}>{name}</span> <span className={styles.addressType}>{addressType}</span><br />
                    <span className={styles.addressLine1}>{addressLine1}</span>, <span className={styles.addressLine2}>{addressLine2}</span>, <span className={styles.city}>{city}</span>, <span className={styles.state}>{state}</span> - <span className={styles.pincode}>{pincode}</span><br />
                    Phone: <span className={styles.mobileNumber}>{mobileNumber}</span>
                </div>
                <div className={`${styles.threeDotsMenu} ${threeDotsMenuOpenId === id || selectedAddress?.id === id ? styles.open : ''}`}>
                    {!selectedAddress && <EllipsisVertical className={styles.threeDots} onClick={e => {
                        e.stopPropagation()
                        setThreeDotsMenuOpenId(id)
                    }} />}
                    <ul className={styles.options}>
                        <li className={styles.edit} onClick={() => {
                            setEditingAddress(address)
                            navigate('/addresses/edit')
                        }}><SquarePen />Edit</li>
                        {!selectedAddress && !defaultShipping && <li className={styles.delete} onClick={() => deleteAddress(id)}>Delete</li>}
                        {!selectedAddress && !defaultShipping && <li className={styles.setDefault} onClick={() => changeDefaultAddress(id)}>Set as default</li>}
                    </ul>
                </div>
            </div>
            {selectedAddress && id === selectedAddress.id && <button className={styles.btnDeliverAddress} onClick={setShowAddressSelector ? () => setShowAddressSelector(false) : () => navigate('/checkout')}>Deliver to this address</button>}
        </div>
    )
}