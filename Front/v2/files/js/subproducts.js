let product = null
let subproduct = null

$(()=>{
    $(".window.subproducts .new").click(()=>{
        $(".window.newSubproduct").css("display", "grid")
    })
    $(".window.newSubproduct form").submit(e => e.preventDefault())
    $(".window.newSubproduct .submit").click(()=>updateSubproct(null))
    $(".window.newSubproduct .cancel").click(()=>$(".window.newSubproduct").css("display", "none"))
})

function actSubproducts(product_){
    //TODO
    product = product_
}
function updateSubproduct(subproduct_){
    subproduct = subproduct_

    let tag = $(".window.newSubproduct .tag input").val()
    let quantity = $(".window.newSubproduct .quantity input").val()

    clearInputError()

    if(tag === "")
        inputError("tag")
    else if(quantity === "")
        inputError("quantity")
    else{
        if(subproduct === null){
            $.post(api + "")
        }
    }
}