function actCart(){
    let list = $(".window.cart .list")

    $.post(api + "cart/get")
}