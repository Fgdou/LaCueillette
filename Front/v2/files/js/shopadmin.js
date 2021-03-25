let product = null
let shop = null

$(()=>{
    $(".window.newProduct > form").submit(e => e.preventDefault())
    $(".window.shopadmin .products .new").click(()=>openProduct())
    $(".window.newProduct .cancel").click(()=>$(".window.newProduct").css("display", "none"))
})

function shopAdminAct(shop_){
    shop = shop_
    $.post(api + "product/get/byStore", {
        store_id: shop.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            parseProductsShop(data)
        }
    }, "json")
}
function parseProductsShop(data){
    let list = $(".window.shopadmin .products .list")
    list.html("")

    for(let i=0; i<data.length; i++){
        let product = data[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("name").html(product.name))
        tr.append($("<td></td>").addClass("quantity").html(product.numberSubproducts))

        let btns = $("<td></td>")

        let edit = $("<img src='files/img/edit.svg' alt='edit'>").addClass("edit")
        let remove = $("<img src='files/img/edit.svg' alt='delete'>").addClass("delete")

        edit.click(()=>{
            openProduct(product)
        })
        remove.click(()=>{
            deleteProduct(product)
        })

        btns.append(edit)
        btns.append(remove)

        tr.append(btns)

        list.append(tr)
    }
}
function deleteProduct(product){
    $.post(api + "product/delete", {
        user_token: token,
        product_id: product.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        shopAdminAct(shop)
    }, "json")
}
function openProduct(product_){
    product = product_
    if(product_ === null) {
        $(".window.newProduct .name input").val("")
        $(".window.newProduct .type input").val("")
        $(".window.newProduct .code input").val("")
    }
    $(".window.newProduct").css("display", "block")
}