// See https://kotlinlang.org/docs/reference/null-safety.html





classclassclassNodeNodeNode(((valvalval parentNodeparentNodeparentNode::: NodeNodeNode???,,, valvalval nodeNamenodeNamenodeName::: StringStringString???))){{{


funfunfungetParentgetParentgetParent((())):::NodeNodeNode???{{{


returnreturnreturnparentNodeparentNodeparentNode


}}}


funfunfungetNamegetNamegetName((())):::StringStringString???{{{


returnreturnreturnnodeNamenodeNamenodeName


}}}


}}}





funfunfunfoofoofoo(((nodenodenode::: NodeNodeNode))):::StringStringString???{{{


valvalvalparentparentparent===nodenodenode...getParentgetParentgetParent((()))?:?:?:returnreturnreturnnullnullnull


valvalvalnamenamename===nodenodenode...getNamegetNamegetName((()))?:?:?:"""THROW IS CURRENTLY NOT SUPPORTEDTHROW IS CURRENTLY NOT SUPPORTEDTHROW IS CURRENTLY NOT SUPPORTED"""//throw IllegalArgumentException("name expected")





returnreturnreturn"""foo returns foo returns foo returns $name$name$name"""


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


// Test simple elvis operator


valvalvalbbb:::StringStringString???==="""asdfasdfasdf"""


// SWIFT: let c = b != nil ? b!.length : -1


valvalvalccc===ififif(((bbb !=!=!= nullnullnull)))bbb...lengthlengthlengthelseelseelse---111


valvalvalddd===bbb???...lengthlengthlength?:?:?:---111


valvalvalerrorerrorerror===ififif(((ccc !=!=!= 444 |||||| ddd !=!=!= 444)))"""ERRORERRORERROR"""elseelseelse"""OKOKOK"""


printlnprintlnprintln((("""$error$error$error: c=: c=: c=$c$c$c (should be 4), d= (should be 4), d= (should be 4), d=$d$d$d (should be 4) (should be 4) (should be 4)""")))





// Elvis return test


valvalvalnode1node1node1===NodeNodeNode(((parentNodeparentNodeparentNode === nullnullnull,,, nodeNamenodeNamenodeName === """node1Namenode1Namenode1Name""")))


valvalvalnode2node2node2===NodeNodeNode(((parentNodeparentNodeparentNode === node1node1node1,,, nodeNamenodeNamenodeName === """node2Namenode2Namenode2Name""")))


printlnprintlnprintln((("""$node1$node1$node1: : : ${${${node1node1node1...getParentgetParentgetParent((()))}}} -  -  - ${${${node1node1node1...getNamegetNamegetName((()))}}}""")))


printlnprintlnprintln((("""$node2$node2$node2: : : ${${${node2node2node2...getParentgetParentgetParent((()))}}} -  -  - ${${${node2node2node2...getNamegetNamegetName((()))}}}""")))


ififif(((foofoofoo(((node1node1node1))) !=!=!= nullnullnull))){{{


printlnprintlnprintln((("""Error1Error1Error1""")))


}}}


ififif(((foofoofoo(((node2node2node2))) !=!=!= """foo returns node2Namefoo returns node2Namefoo returns node2Name"""))){{{


printlnprintlnprintln((("""Error2Error2Error2""")))


}}}





// Test throw


valvalvalnode3node3node3===NodeNodeNode(((parentNodeparentNodeparentNode === node2node2node2,,, nodeNamenodeNamenodeName === nullnullnull)))


printlnprintlnprintln((("""$node3$node3$node3: : : ${${${node3node3node3...getParentgetParentgetParent((()))}}} -  -  - ${${${node3node3node3...getNamegetNamegetName((()))}}}""")))


printlnprintlnprintln(((foofoofoo(((node3node3node3))))))


}}}


<EOF>