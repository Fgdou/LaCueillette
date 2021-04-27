$(()=>{
    let postalcode_input = $(".window.select .postalcode input")
    let select = $(".window.select .city select")


    setCitySelect(postalcode_input, select)

    $(".window.select form").submit(e=>e.preventDefault())
    $(".window.select .submit").click(()=>{
        let pc = $(".window.select .postalcode input").val()
        let city_ = $(".window.select .city select").val()

        clearInputError()

        if(pc === "")
            inputError("postalcode")
        else if(city_ === "")
            inputError("city")
        else{
            postalcode = pc
            city = city_
            $("header .address span").html(city)
            openWindow("search")
        }
    })
    $("header .address").click(()=>{
        openWindow("select")
        $(".window.select .postalcode input").val(postalcode)
        setCity(postalcode, city, select)
    })
})

function setCitySelect(postalcode_input, select_input){
    postalcode_input.on("input", (e)=>{
        let value = postalcode_input.val()
        searchCity(value, select_input)
    })
}
function setCity(pc, name, output){
    searchCity(pc, output, ()=>{
        output.val(name)
    })
}
function searchCity(postalcode, output, f){
    if(postalcode.length === 5){
        $.get("https://datanova.legroupe.laposte.fr/api/records/1.0/search/?dataset=laposte_hexasmal&q="+postalcode+"&facet=code_commune_insee&facet=nom_de_la_commune&facet=code_postal&facet=ligne_5",{},data=>{
            output.html("")

            if(data.facet_groups){
                let cities = data.facet_groups[2].facets

                for(let i=0; i<cities.length; i++){
                    let city = cities[i]
                    let name = city.name

                    output.append($("<option value='"+name+"'></option>").html(name))

                    if(output.val() == "")
                        output.val(name)

                }
                if(f)
                    f()
            }

        }, "json")
    }else{
        output.html("")
    }
}