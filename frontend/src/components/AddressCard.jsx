import { useNavigate } from 'react-router'
import threeDotsIcon from '../assets/icons/three-dots.svg'
import styles from '../styles/ManageAddresses.module.css'
import { useContext } from 'react'
import AddressesContext from '../contexts/AddressesContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import pencilEditIcon from '../assets/icons/edit-pencil.svg'
import useMediaQuery from '../hooks/useMediaQuery'

export default function AddressCard({ address, threeDotsMenuOpenId, setThreeDotsMenuOpenId, desktopSelectStyles, isSelectMode, setShowAddressSelector }) {

    const { addresses, setAddresses, selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const { addressId, name, addressLine1, addressLine2, pincode, city, state, mobileNumber, addressType, isDefaultShipping } = address
    const navigate = useNavigate()
    const { setEditingAddress } = useContext(AddressesContext)
    const { authFetch } = useAuthFetch()
    // const isMobile = useMediaQuery('(max-width: 767px)')

    async function deleteAddress() {
        const res = await authFetch(`http://localhost:8080/api/addresses/${addressId}`, { method: 'DELETE' })
        if (res.ok) {
            setAddresses(addresses.filter(address => address.addressId != addressId))
            navigate('/addresses')
        }
    }

    async function changeDefaultAddress() {
        const res = await authFetch(`http://localhost:8080/api/addresses/${addressId}/default`, { method: 'PUT' })
        if (res.ok) {
            setAddresses(addresses => addresses.map(addressData => ({ ...addressData, isDefaultShipping: addressData.addressId === addressId })))
        }
    }

    return (
        <div className={`${styles.addressCard} ${desktopSelectStyles?.addressCard}`} id={addressId === selectedAddress?.addressId ? styles.selected : ''} onClick={selectedAddress ? e => {
            document.getElementById(styles.selected).removeAttribute('id')
            e.currentTarget.id = styles.selected
            setSelectedAddress(address)
        } : undefined}>
            <div className={styles.addressSubcard}>
                {selectedAddress && <div className={styles.customRadio}></div>}
                <div className={styles.address}>
                    {isDefaultShipping && <><div className={styles.defaultBadge}>Default</div><br /></>}
                    <span className={styles.name}>{name}</span> <span className={styles.addressType}>{addressType}</span><br />
                    <span className={styles.addressLine1}>{addressLine1}</span>, <span className={styles.addressLine2}>{addressLine2}</span>, <span className={styles.city}>{city}</span>, <span className={styles.state}>{state}</span> - <span className={styles.pincode}>{pincode}</span><br />
                    Phone: <span className={styles.mobileNumber}>{mobileNumber}</span>
                </div>
                <div className={`${styles.threeDotsMenu} ${threeDotsMenuOpenId === addressId || selectedAddress?.addressId === addressId ? styles.open : ''}`}>
                    {!selectedAddress && <img className={styles.threeDots} src={threeDotsIcon} alt="More options" onClick={e => {
                        e.stopPropagation()
                        setThreeDotsMenuOpenId(addressId)
                    }} />}
                    <ul className={styles.options}>
                        <li className={styles.edit} onClick={() => {
                            setEditingAddress(address)
                            navigate('/addresses/edit')
                        }}><img src={pencilEditIcon} />Edit</li>
                        {!selectedAddress && !isDefaultShipping && <li className={styles.delete} onClick={deleteAddress}>Delete</li>}
                        {!selectedAddress && !isDefaultShipping && <li className={styles.setDefault} onClick={changeDefaultAddress}>Set as default</li>}
                    </ul>
                </div>
            </div>
            {selectedAddress && addressId === selectedAddress.addressId && <button className={styles.btnDeliverAddress} onClick={setShowAddressSelector? () => setShowAddressSelector(false) : () => navigate('/checkout')}>Deliver to this address</button>}
        </div>
    )
}