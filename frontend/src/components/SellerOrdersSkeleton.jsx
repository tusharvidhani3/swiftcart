import SellerOrderCardSkeleton from "./SellerOrderCardSkeleton";

export default function SellerOrdersSkeleton() {

    return (
        <>
            {new Array(10).fill(null).map((_, i) => <SellerOrderCardSkeleton key={i} />)}
        </>
    )
}