export default function Toast({ children }) {
    return (
        <div id="toast" className={children?"show":""}>{children}</div>
    )
}