import errorImg from '../assets/images/error.jpg'
import styles from '../styles/ErrorMessage.module.css'

export default function ErrorMessage({ type }) {

  let errorMsg = null

  switch (type) {
    case 'forbidden':
      errorMsg = "Access Denied: You do not have permission to perform this action"
      break
    default:
      errorMsg = "Something went wrong. Please try again.";
      break
  }

  return (
    <div className={styles.error}>
      <img src={errorImg} alt="error" />
      <h1>{errorMsg}</h1>
    </div>
  )
}