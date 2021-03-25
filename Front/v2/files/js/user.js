$(()=>{
    $(".window.user .logout").click(()=>logout())
})

function userAct(){
    $(".window.user .name input").val(user.name)
    $(".window.user .surname input").val(user.surname)
    $(".window.user .tel input").val(user.tel)
    $(".window.user .email").html(user.mail)
    $(".window.user .creation").html(dateToString(user.created))

    $.post(api + "user/get/allAddresses", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else {

        }
    }, "json")
}