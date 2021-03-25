address = null

$(()=>{
    $(".window.user .logout").click(()=>logout())
    $(".window.user .newAddress > form").submit(e => e.preventDefault())
    $(".window.user .newAddress .submit").cleanData(createChangeAddress)
})

function createChangeAddress(){
    let number = $(".window.user .newAddress .number input").val()
    let way = $(".window.user .newAddress .way input").val()
    let city = $(".window.user .newAddress .city input").val()
    let postalcode = $(".window.user .newAddress .postalcode input").val()
    let state = $(".window.user .newAddress .state input").val()

    if(address === null){
        $.post(api + "/address/new", {
            user_token: token,
            number: number,
            way: way,
            city: city,
            postalcode: postalcode,
            state: state
        }, data=>{
            if(data.error)
                errorPopup(data.error)
            else{
                $(".window.user .newAddress").css("display", "none")
                userAct()
            }
        }, "json")
    }
}
function userAct(){
    $(".window.user .name input").val(user.name)
    $(".window.user .surname input").val(user.surname)
    $(".window.user .tel input").val(user.tel)
    $(".window.user .email").html(user.mail)
    $(".window.user .creation").html(dateToString(user.created))
    $(".window.user .addresses .new").click(()=>openAddress())

    $.post(api + "user/get/allAddresses", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else {
            //TODO
        }
    }, "json")
}

function openAddress(address_){
    address = address_
    if(address_ === undefined){
        $(".window.user .newAddress .number input").val("")
        $(".window.user .newAddress .way input").val("")
        $(".window.user .newAddress .city input").val("")
        $(".window.user .newAddress .postalcode input").val("")
        $(".window.user .newAddress .state input").val("")

        $(".window.user .newAddress").css("display", "block")
    }
}