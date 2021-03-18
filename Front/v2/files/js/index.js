let user = null
let page = null
let token = null
let url = "https://lacueillette.ml/v2/"
let api = "https://lacueillette.ml/api/"

$(()=>{
    closeWindows();

    $("header .account").click(()=>{
        if(user === null)
            openWindow("login")
    })
    $("header .logo").click(()=>{
        window.location.replace(url)
    })
    $(".window.login .submit button").click(()=>{
        let bt = $(".window.login .submit")

        clearInputError()

        let email = $(".window.login .email input").val()
        let password = $(".window.login .password input").val()

        if(email === "")
            inputError("email")
        else if(password === "")
            inputError("password")
        else{
            bt.prop("disabled", true);

            $.post(api + "user/login", {
                email: email,
                password: password
            }, (data)=>{
                bt.prop("disabled", false);
                if(data.error){
                    if(data.error === "Wrong password")
                        inputError("password")
                    else if(data.error === "User not found")
                        inputError("email")
                    else
                        errorPopup(data.error)
                }else{
                    token = data
                    user = data.user
                    closeWindows()
                }
            },"json");
        }
    })
})

function openWindow(name){
    closeWindows()
    page = name
    $(".window."+name).css("display", "block")
}
function closeWindows(){
    $(".window").css("display", "none")
}
function inputError(name){
    $(".input."+name).addClass("error")
}
function clearInputError(){
    $(".input").removeClass("error")
}
function errorPopup(msg){
    console.log(msg)
}