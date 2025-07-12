import styles from '../styles/index.module.css'

export default function Toast({children}) {
    return (
        <div id={styles.toast} className={children?styles.show:""}>{children}</div>
    )
}