import { Skeleton } from "./ui/skeleton";

export default function OrderCardSkeleton() {

    return (
        <div className="flex flex-col rounded-2xl w-1/2 h-8" onClick={() => navigate(`./${id}`)}>
            <div className="">
                <Skeleton className="" />
                <Skeleton className="" />
                <Skeleton className="" />
            </div>
            <div className={styles.orderItemsContainer}>
                {new Array(10).fill(null).map((_, i) => <OrderItemCardSkeleton key={i} />)}
            </div>
        </div>
    )
}