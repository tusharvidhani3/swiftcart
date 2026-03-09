import { useNavigate } from 'react-router'
import threeDotsIcon from '../assets/icons/three-dots.svg'
import styles from '../styles/ManageAddresses.module.css'
import { useContext } from 'react'
import AddressesContext from '../contexts/AddressesContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import pencilEditIcon from '../assets/icons/edit-pencil.svg'
import { apiBaseUrl } from '../config'

export default function AddressCard({ address, threeDotsMenuOpenId, setThreeDotsMenuOpenId, desktopSelectStyles, setShowAddressSelector }) {

    const { addresses, setAddresses, selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const { id, name, addressLine1, addressLine2, pincode, city, state, mobileNumber, addressType, defaultShipping } = address
    const navigate = useNavigate()
    const { setEditingAddress } = useContext(AddressesContext)
    const { authFetch } = useAuthFetch()

    async function deleteAddress() {
        await authFetch(`${apiBaseUrl}/api/addresses/${id}`, { method: 'DELETE' })
        setAddresses(addresses.filter(address => address.id != id))
        navigate('/addresses')
    }

    async function changeDefaultAddress() {
        const res = await authFetch(`${apiBaseUrl}/api/addresses/${id}/default`, { method: 'PUT' })
        setAddresses(addresses => addresses.map(addressData => ({ ...addressData, defaultShipping: addressData.id === id })))
    }

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
                    {!selectedAddress && <img className={styles.threeDots} src={threeDotsIcon} alt="More options" onClick={e => {
                        e.stopPropagation()
                        setThreeDotsMenuOpenId(id)
                    }} />}
                    <ul className={styles.options}>
                        <li className={styles.edit} onClick={() => {
                            setEditingAddress(address)
                            navigate('/addresses/edit')
                        }}><img src={pencilEditIcon} />Edit</li>
                        {!selectedAddress && !defaultShipping && <li className={styles.delete} onClick={deleteAddress}>Delete</li>}
                        {!selectedAddress && !defaultShipping && <li className={styles.setDefault} onClick={changeDefaultAddress}>Set as default</li>}
                    </ul>
                </div>
            </div>
            {selectedAddress && id === selectedAddress.id && <button className={styles.btnDeliverAddress} onClick={setShowAddressSelector? () => setShowAddressSelector(false) : () => navigate('/checkout')}>Deliver to this address</button>}
        </div>
    )
}