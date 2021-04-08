let product2 = null
let subproduct = null

$(()=>{
    $(".window.subproducts .new").click(()=>{
        showNewSubproduct(null)
    })
    $(".window.newSubproduct form").submit(e => e.preventDefault())
    $(".window.newSubproduct .submit").click(()=>updateSubproduct(null))
    $(".window.newSubproduct .cancel").click(()=>$(".window.newSubproduct").css("display", "none"))
})

function actSubproducts(product_){
    product2 = product_

    $.post(api + "subproduct/get", {
        product_id: product2.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            parseSubProducts(data)
    }, "json")
}
function parseSubProducts(data){
    let list = $(".window.subproducts .list")

    list.html("")

    for(let i=0; i<data.length; i++){
        let sp = data[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("quantity").html(sp.quantity))
        tr.append($("<td></td>").addClass("tag").html(sp.tag))

        list.append(tr)
    }
}
function showNewSubproduct(subproduct_){
    subproduct = subproduct_

    let tag = $(".window.newSubproduct .tag input")
    let quantity = $(".window.newSubproduct .quantity input")

    if(subproduct === null){
        tag.val("")
        quantity.val("")
    }else{
        tag.val(subproduct.special_tag)
        quantity.val(subproduct.quantity)
    }

    $(".window.newSubproduct").css("display", "grid")
}
function updateSubproduct(){

    let tag = $(".window.newSubproduct .tag input").val()
    let quantity = $(".window.newSubproduct .quantity input").val()

    clearInputError()

    if(tag === "")
        inputError("tag")
    else if(quantity === "")
        inputError("quantity")
    else{
        if(subproduct === null){
            $.post(api + "subproduct/new", {
                user_token: token,
                tag: tag,
                quantity: quantity,
                product_id: product2.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else{
                    $(".window.newSubproduct").css("display", "none")
                    actSubproducts(product2)
                }
            }, "json")
        }
    }
}