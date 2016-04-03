parser grammar HNBTParser;

options {tokenVocab = HNBTLexer;}

@header {
package me.kenzierocks.hnbt.grammar;

import me.kenzierocks.hnbt.util.ByteList;
import me.kenzierocks.hnbt.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jnbt.*;
}
root
    returns [CompoundTag rootTag]
    : CWS? CompoundTagType CWS RootName WS? TagToValue WS? tag=captureVal["compound","root"] WS? EOF {$rootTag = (CompoundTag) $tag.tag;} ;
captureTag[boolean inCompound]
    returns [Tag tag]
    locals [String type,String name]
    : WS? TagType {$type = $TagType.text;}
                    // N.B. TagType is allowed b/c types may be names as well
                    ({$inCompound}? WS nameCap=(TagType|TagName) {$name = $nameCap.text;} | {$name = "";})
                                      WS? TagToValue WS? cap=captureVal[$type,$name] WS? {$tag = $cap.tag;} ;

captureVal[String type, String name]
    returns [Tag tag]
    : {$type.equals("compound")}? capC=captureCompound[$name] {$tag = $capC.tag;}
    | {$type.equals("byte")}? val=INTLIKEVAL {$tag = new ByteTag($name, Byte.parseByte($val.text));}
    | {$type.equals("byte-array")}? capBA=captureByteArray[$name] {$tag = $capBA.tag;}
    | {$type.equals("short")}? val=INTLIKEVAL {$tag = new ShortTag($name, Short.parseShort($val.text));}
    | {$type.equals("int")}? val=INTLIKEVAL {$tag = new IntTag($name, Integer.parseInt($val.text));}
    | {$type.equals("int-array")}? capIA=captureIntArray[$name] {$tag = $capIA.tag;}
    | {$type.equals("long")}? val=INTLIKEVAL {$tag = new LongTag($name, Long.parseLong($val.text));}
    | {$type.equals("float")}? val=FLOATLIKEVAL {$tag = new FloatTag($name, Float.parseFloat($val.text));}
    | {$type.equals("double")}? val=FLOATLIKEVAL {$tag = new DoubleTag($name, Double.parseDouble($val.text));}
    | {$type.equals("list")}? capL=captureList[$name] {$tag = $capL.tag;}
    | {$type.equals("string")}? str=captureStringVal {$tag = new StringTag($name, $str.val);}
    ;
captureCompound[String name]
    returns [CompoundTag tag]
    locals [Map<String,Tag> tagMap]
    @init
    {
        $tagMap = new HashMap<>();
    }
    @after
    {
        $tag = new CompoundTag($name, $tagMap);
    }
    : OpenCompound WS? ( | (capInCompoundTag WS? ItemSep WS?)* capInCompoundTag WS?) CloseCompound ;
capInCompoundTag: tag=captureTag[true] {$captureCompound::tagMap.put($tag.tag.getName(), $tag.tag);} ;

captureList[String name]
    returns [ListTag tag]
    locals [List<Tag> tags]
    @init
    {
        $tags = new ArrayList<>();
    }
    @after
    {
        Set<Class> classes = $tags.stream().map(Object::getClass).distinct().collect(Collectors.toSet());
        if (classes.size() > 1) {
            // TODO: add lineno info
            throw new IllegalStateException("Multiple types in list: " + classes);
        }
        if (classes.isEmpty()) {
            if (!$tags.isEmpty()) {
                throw new IllegalStateException("no classes in tags list...?");
            }
            classes = Collections.singleton(EndTag.class);
        }
        Class<? extends Tag> tagClass = (Class<? extends Tag>) classes.iterator().next();
        $tag = new ListTag($name, tagClass, $tags);
    }
    : OpenList WS? ( | (capInListTag WS? ItemSep WS?)* capInListTag WS?) CloseList ;
capInListTag: tag=captureTag[false] {$captureList::tags.add($tag.tag);} ;

captureByteArray[String name]
    returns [ByteArrayTag tag]
    locals [ByteList bytes]
    @init
    {
        $bytes = new ByteList();
    }
    @after
    {
        $tag = new ByteArrayTag($name, $bytes.toArray());
    }
    : OpenList WS? ( | (capInByteArray WS? ItemSep WS?)* capInByteArray WS?) CloseList ;
capInByteArray: val=INTLIKEVAL {$captureByteArray::bytes.add(Byte.parseByte($val.text));} ;

captureIntArray[String name]
    returns [IntArrayTag tag]
    locals [IntStream.Builder ints]
    @init
    {
        $ints = IntStream.builder();
    }
    @after
    {
        $tag = new IntArrayTag($name, $ints.build().toArray());
    }
    : OpenList WS? ( | (capInByteArray WS? ItemSep WS?)* capInByteArray WS?) CloseList ;
capInIntArray: val=INTLIKEVAL {$captureIntArray::ints.add(Integer.parseInt($val.text));} ;

captureStringVal
    returns [String val]
    : str=STRING_ONELINE?
        {
        String v = $str.text;
        $val = StringUtil.unescapeString(v.substring(1, v.length() - 1));
        }
    ; // TODO MULTILINE STRINGS OR ADDITION ACROSS LINES OR SOMETHING

