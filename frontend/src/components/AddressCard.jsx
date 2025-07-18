import { useNavigate } from 'react-router'
import threeDotsIcon from '../assets/icons/three-dots.svg'

export default function AddressCard({ address, isSelected, addresses, setAddresses }) {

    const {addressId, name, addressLine1, addressLine2, pincode, city, state, mobileNumber, addressType, isDefaultShipping} = address
    const navigate = useNavigate()

    async function deleteAddress() {
        const res = await fetch('http://localhost:8080/api/addresses', {
            method: 'DELETE',
            credentials: 'include'
        })
        if(res.ok) {
            setAddresses(addresses.filter(address => address.addressId != addressId))
            navigate('/addresses')
        }
    }

    async function setDefaultAddress() {
        const res = await fetch(`http://localhost:8080/api/addresses/${addressId}/default`, {
            method: 'PUT',
            credentials: 'include'
        })
        
    }

    return (
        <div className={styles.addressCard} >
            <div className={styles.customRadio}></div>
            <div className={styles.address}>
                <div className={styles.defaultLabel}>Default</div><br />
                <span className={styles.name}>{name}</span> <span className={styles.addressType}>{addressType}</span><br />
                <span className={styles.addressLine1}>{addressLine1}</span>, <span className={styles.addressLine2}>{addressLine2}</span>, <span className={styles.city}>{city}</span>, <span className={styles.state}>{state}</span> - <span className={styles.pincode}>{pincode}</span><br />
                Phone: <span className={styles.mobileNumber}>{mobileNumber}</span>
            </div>
            <div className={styles.threeDotsMenu}>
                <img className={styles.threeDots} src={threeDotsIcon} alt="More options" />
                <ul className={styles.options}>
                    <li className={styles.edit} onClick={() => {
                        setEditingAddress(address)
                        navigate('./edit')
                        }}>Edit</li>
                    <li className={styles.delete} onClick={deleteAddress}>Delete</li>
                    <li className={styles.setDefault} onClick={{setDefault}}>Set as default</li>
                </ul>
            </div>
        </div>
    )
}