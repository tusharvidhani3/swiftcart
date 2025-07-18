import styles from '../styles/AddAddress.module.css'

export default function AddAddress() {

    return (
        <form id={styles.addressForm}>
            <div>
                <label htmlFor="name">Full name</label>
                <input type="text" id="name" name="name" />
            </div>
            <div>
                <label htmlFor="mobile-number">Mobile number</label>
                <input type="text" id="mobile-number" name="mobileNumber" />
            </div>
            <div>
                <label htmlFor="pincode">Pincode</label>
                <input type="text" id="pincode" name="pincode" />
            </div>
            <div>
                <label htmlFor="address-line-1">Flat, House no., Building, Company, Apartment</label>
                <input type="text" id="address-line-1" name="addressLine1" />
            </div>
            <div>
                <label htmlFor="address-line-2">Area, Street, Sector, Village</label>
                <input type="text" id="address-line-2" name="addressLine2" />
            </div>
            <div>
                <label htmlFor="city">City/District/Town</label>
                <input type="text" id="city" name="city" />
            </div>
            <div>
                <label htmlFor="state">State</label>
                <input type="text" id="state" name="state" />
            </div>
            <div>
                <label htmlFor="address-type">Address Type</label>
                <input type="radio" id="home" name="addressType" value="HOME" checked />
                <label htmlFor="home">Home</label>
                <input type="radio" id="work" name="addressType" value="WORK" />
                <label htmlFor="work">Work</label>
            </div>
            <button className={styles.btnAddAddress}>Add Address</button>
        </form>
    )
}